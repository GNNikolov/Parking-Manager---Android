package com.joron.parkingmanager

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.joron.parkingmanager.adapter.ViewPagerAdapter
import com.joron.parkingmanager.authentication.FirebaseAuthManager
import com.joron.parkingmanager.bluetooth.BluetoothGPSReceiver
import com.joron.parkingmanager.bluetooth.BluetoothLeScanner
import com.joron.parkingmanager.databinding.ActivityMainBinding
import com.joron.parkingmanager.fragment.LogInOutDialog
import com.joron.parkingmanager.models.BleState
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.models.SignInResponseModel
import com.joron.parkingmanager.util.Util
import com.joron.parkingmanager.viewmodel.BleStateViewModel
import com.joron.parkingmanager.viewmodel.CarViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bluetooth_indicator.*
import kotlinx.android.synthetic.main.content.*
import kotlinx.android.synthetic.main.progressive_layout.*

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var leScanner: BluetoothLeScanner
    private lateinit var activityContentBinding: ActivityMainBinding
    private val bleStateViewModel: BleStateViewModel by viewModels()
    private val authViewModel: UserAuthViewModel by viewModels()
    private val carViewModel: CarViewModel by viewModels()
    private lateinit var authManager: FirebaseAuthManager
    var connectedToBleDevice = false
    private val bluetoothStateObserver = Observer<BleState> {
        if (::activityContentBinding.isInitialized) {
            activityContentBinding.state = it
            activityContentBinding.executePendingBindings()
        }
        if (it is BleState.BleConnected && !connectedToBleDevice){
            connectedToBleDevice = true
        }
    }
    private var mMenu: Menu? = null
    private val gpsToggleObserver = Observer<Boolean> {enabled ->
        if (enabled) {
            bleStateViewModel.bleLiveData.value = BleState.LocationEnabled
            if (!leScanner.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                leScanner.scanLeDevice(true)
            }
        }
    }
    private val userLoadObserver = Observer<FirebaseUser?> {
        updateLoginMenu(it)
        if (it == null) {
            LogInOutDialog.showSignInDialog(this, authManager)
        }
    }
    private val signInObserver = Observer<ResponseModel> {
        when (it) {
            ResponseModel.Loading -> showProgressiveView(true)
            is SignInResponseModel -> {
                showProgressiveView(false)
                updateLoginMenu(it.data)
                showSignInResultDialog(true)
            }
            is ResponseModel.Error -> {
                showProgressiveView(false)
                showSignInResultDialog(false, it.code)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        activityContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        setContentView(activityContentBinding.root)
        setSupportActionBar(toolbar)
        bleView.initViews(contentHolder, activeBluetooth, statusBluetooth)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        pager.adapter = ViewPagerAdapter(this)
        tab_layout.addOnTabSelectedListener(this)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            val text = if (position == 0) getString(R.string.cars) else getString(R.string.history)
            tab.text = text
            pager.setCurrentItem(tab.position, true)
        }.attach()
        authManager = FirebaseAuthManager(this, authViewModel, carViewModel)
        leScanner = BluetoothLeScanner(this, bleStateViewModel)
        with(bleStateViewModel) {
            bleLiveData.observe(this@MainActivity, bluetoothStateObserver)
            locationEnableLiveData().observe(this@MainActivity, gpsToggleObserver)
        }
        if (leScanner.checkPermissions()) {
            bleStateViewModel.setGPSToggleValue(isGPSEnabled(this.application))
        } else {
            ActivityCompat.requestPermissions(
                this,
                BluetoothLeScanner.SPERMISSIONS,
                PERMISSION_REQUEST_CODE
            )
        }
        BluetoothGPSReceiver(this, bleStateViewModel, leScanner)
        if (!isGPSEnabled(application)) {
            bleStateViewModel.bleLiveData.value = BleState.NoLocation
        }
        bleView.iconAction?.setOnClickListener {
            if (!isGPSEnabled(this.application)) {
                promptLocationAccess()
            }
        }
        authViewModel.userLoadLiveData.observe(this, userLoadObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        mMenu = menu
        authViewModel.initUser()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_login -> authManager.signIn()
            R.id.item_ble_show_hide -> {
                bleView.let {
                    if (it.isVisible) {
                        it.hide()
                    } else {
                        it.showView()
                    }
                }
            }
            R.id.item_logout -> LogInOutDialog.showSignOutDialog(this, authManager)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        bleStateViewModel.setGPSToggleValue(isGPSEnabled(this.application))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FirebaseAuthManager.RC_SIGN_IN) {
            authViewModel.handleSignIn(resultCode).observe(this, signInObserver)
        }
    }

    override fun onDestroy() {
        bleStateViewModel.bleLiveData.removeObserver(bluetoothStateObserver)
        bleStateViewModel.locationEnableLiveData().removeObserver(gpsToggleObserver)
        super.onDestroy()
    }

    private fun promptLocationAccess() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        startActivity(this)
    }

    private fun updateLoginMenu(user: FirebaseUser?) {
        toolbar.title = if (user != null) {
            user.phoneNumber
        } else getString(R.string.not_signed_in)
        mMenu.let {
            val visible = user != null
            it?.findItem(R.id.item_logout)?.isVisible = visible
            it?.findItem(R.id.item_login)?.isVisible = !visible
        }
    }

    fun sendToBleDevice() = leScanner.rotate()

    private fun showSignInResultDialog(successful: Boolean, errorCode: Int = -1) {
        val title = if (successful)
            getString(R.string.sign_in_successful) else "Error: $errorCode"
        Util.buildDialog(this, title)
            .setPositiveButton(getString(R.string.ok), null)
            .create()
            .show()
    }

    private fun showProgressiveView(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        layout_loading.visibility = visibility
    }

    fun initFab(tabPos: Int) {
        val drawable = if (tabPos == 0)
            getDrawable(android.R.drawable.ic_input_add)
        else
            getDrawable(R.drawable.ic_filter_list_24)
        fab.setImageDrawable(drawable)
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 101
        const val PERMISSION_REQUEST_CODE = 100
        fun isGPSEnabled(context: Context): Boolean {
            val service: LocationManager =
                context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            initFab(it.position)
        }
    }
}
