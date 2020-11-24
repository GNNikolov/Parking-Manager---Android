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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.joron.parkingmanager.adapter.ViewPagerAdapter
import com.joron.parkingmanager.authentication.FirebaseAuthManager
import com.joron.parkingmanager.bluetooth.BluetoothGPSReceiver
import com.joron.parkingmanager.bluetooth.BluetoothLeScanner
import com.joron.parkingmanager.databinding.ActivityMainBinding
import com.joron.parkingmanager.fragment.CarPromptDialog
import com.joron.parkingmanager.fragment.LogInOutDialog
import com.joron.parkingmanager.models.CarResponseModel
import com.joron.parkingmanager.models.State
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.models.SignInResponseModel
import com.joron.parkingmanager.networking.ApiClient.Companion.passJWT
import com.joron.parkingmanager.networking.NetworkService
import com.joron.parkingmanager.util.Util
import com.joron.parkingmanager.viewmodel.BluetoothLocationViewModel
import com.joron.parkingmanager.viewmodel.CarViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bluetooth_indicator.*
import kotlinx.android.synthetic.main.content.*
import kotlinx.android.synthetic.main.progressive_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var leScanner: BluetoothLeScanner
    private lateinit var activityContentBinding: ActivityMainBinding

    private val bluetoothLocationViewModel: BluetoothLocationViewModel by viewModels()
    private val authViewModel: UserAuthViewModel by viewModels()
    private val carViewModel: CarViewModel by viewModels()

    private lateinit var authManager: FirebaseAuthManager
    var connectedToBleDevice = false
    private val bluetoothStateObserver = Observer<State> {
        if (::activityContentBinding.isInitialized) {
            activityContentBinding.state = it
            activityContentBinding.executePendingBindings()
        }
        if (it is State.BleConnected && !connectedToBleDevice){
            connectedToBleDevice = true
        }else if (it is State.NotDeviceFound) {
            showNoBleDevicesFoundDialog()
        }
    }
    private var mMenu: Menu? = null
    private val apiClient by lazy {
        NetworkService.apiClient
    }
    private val gpsToggleObserver = Observer<Boolean> {enabled ->
        if (enabled) {
            bluetoothLocationViewModel.stateLiveData.value = State.LocationEnabled
            if (!leScanner.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                leScanner.scanLeDevice(true)
            }
        }
    }

    private val userLoadObserver = Observer<FirebaseUser?> {
        updateMenu(it)
        if (it == null) {
            LogInOutDialog.showSignInDialog(this, authManager)
        }
    }

    private val signInObserver = Observer<ResponseModel> {
        when (it) {
            ResponseModel.Loading -> showProgressiveView(true)
            is SignInResponseModel -> {
                showProgressiveView(false)
                updateMenu(it.data)
                showSignInResultDialog(true)
                loadCarsFromBackend()
            }
            is ResponseModel.Error -> {
                showProgressiveView(false)
                showSignInResultDialog(false, it.code)
            }
        }
    }

    private fun loadCarsFromBackend() {
        val carFetchObserver = Observer<ResponseModel> {
            if (it is CarResponseModel) {
                val data = it.data
                if (data.isNotEmpty()){
                    carViewModel.insertAll(data)
                }
            }
        }
        carViewModel.fetchAll().observe(this, carFetchObserver)
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
        leScanner = BluetoothLeScanner(this, bluetoothLocationViewModel)
        with(bluetoothLocationViewModel) {
            stateLiveData.observe(this@MainActivity, bluetoothStateObserver)
            locationEnableLiveData().observe(this@MainActivity, gpsToggleObserver)
        }
        if (leScanner.checkPermissions()) {
            bluetoothLocationViewModel.setGPSToggleValue(isGPSEnabled(this.application))
        } else {
            ActivityCompat.requestPermissions(
                this,
                BluetoothLeScanner.SPERMISSIONS,
                PERMISSION_REQUEST_CODE
            )
        }
        BluetoothGPSReceiver(this, bluetoothLocationViewModel, leScanner)
        if (!isGPSEnabled(application)) {
            bluetoothLocationViewModel.stateLiveData.value = State.NoLocation
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
            R.id.item_report_plate -> CarPromptDialog.reportPlate(this) {
                reportPlate(it)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reportPlate(plate: String) = lifecycleScope.launch(Dispatchers.IO) {
        Util.getJWTToken(this@MainActivity.applicationContext)?.let { token ->
            val result = apiClient.reportPlate(plate, passJWT(token))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        bluetoothLocationViewModel.setGPSToggleValue(isGPSEnabled(this.application))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FirebaseAuthManager.RC_SIGN_IN) {
            authViewModel.handleSignIn(resultCode).observe(this, signInObserver)
        }
    }

    private fun promptLocationAccess() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        startActivity(this)
    }

    private fun updateMenu(user: FirebaseUser?) {
        toolbar.title = if (user != null) {
            user.phoneNumber
        } else getString(R.string.not_signed_in)
        mMenu.let {
            val visible = user != null
            it?.findItem(R.id.item_logout)?.isVisible = visible
            it?.findItem(R.id.item_login)?.isVisible = !visible
            it?.findItem(R.id.item_report_plate)?.isVisible = visible
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

    private fun showNoBleDevicesFoundDialog() {
        val title = getString(R.string.no_ble_devices_found)
        val message = getString(R.string.scan_again)
        Util.buildDialog(this, title, message).also { builder ->
            builder.setCancelable(false)
            builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                leScanner.scanLeDevice(true)
            }
            builder.setNegativeButton(getString(R.string.cancel), null)
        }.create().show()
    }
}
