package com.joron.parkingmanager

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
import com.joron.parkingmanager.viewmodel.BleStateViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content.*
import kotlinx.android.synthetic.main.content.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var leScanner: BluetoothLeScanner
    private lateinit var activityContentBinding: ActivityMainBinding
    private val bleStateViewModel: BleStateViewModel by viewModels()
    private val authViewModel: UserAuthViewModel by viewModels()
    private lateinit var authManager: FirebaseAuthManager
    private val observer = Observer<BleState> {
        if (::activityContentBinding.isInitialized){
            activityContentBinding.state = it
            activityContentBinding.executePendingBindings()
        }
    }
    private var mMenu: Menu? = null
    private val gpsToggleObserver = Observer<Boolean> {
        if (it){
            bleStateViewModel.bleLiveData.value = BleState.LocationEnabled
            if(!leScanner.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }else{
                leScanner.scanLeDevice(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        activityContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        setContentView(activityContentBinding.root)
        setSupportActionBar(toolbar)
        bleView.contentView = content
        supportActionBar?.setDisplayShowTitleEnabled(false)
        authManager = FirebaseAuthManager(this, authViewModel)
        leScanner = BluetoothLeScanner(this, bleStateViewModel)
        with(bleStateViewModel){
            bleLiveData.observe(this@MainActivity, observer)
            locationEnableLiveData().observe(this@MainActivity, gpsToggleObserver)
        }
        if (leScanner.checkPermissions()){
            bleStateViewModel.setGPSToggleValue(isGPSEnabled(this.application))
        }else{
            ActivityCompat.requestPermissions(this, BluetoothLeScanner.SPERMISSIONS, PERMISSION_REQUEST_CODE)
        }
        BluetoothGPSReceiver(this, bleStateViewModel, leScanner)
        if (!isGPSEnabled(this.application)){
            bleStateViewModel.bleLiveData.value = BleState.NoLocation
        }
        val pager = activityContentBinding.mainLayout.pager
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = "Title: $position"
            pager.setCurrentItem(tab.position, true)
        }.attach()
        bleView.iconBluetooth.setOnClickListener {
            if (!isGPSEnabled(this.application)){
                promptLocationAccess()
            }
        }
        authViewModel.userLiveData.observe(this, Observer {
            toolbar.title = if (it != null) {it.phoneNumber} else getString(R.string.not_signed_in)
            updateLoginMenu(it)
        })
        val dialog = LogInOutDialog()
        dialog.show(supportFragmentManager, "mDialog")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        mMenu = menu
        authViewModel.initUser()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_login -> authManager.signIn()
            R.id.item_ble_show_hide -> {
                bleView?.let {
                    if (it.isVisible) {
                        it.hideView()
                    }else{
                        it.showView()
                    }
                }
            }
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
        authManager.handleSignInResult(requestCode, resultCode)
    }

    override fun onDestroy() {
        bleStateViewModel.bleLiveData.removeObserver(observer)
        bleStateViewModel.locationEnableLiveData().removeObserver(gpsToggleObserver)
        super.onDestroy()
    }

    private fun promptLocationAccess() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        startActivity(this)
    }

    private fun updateLoginMenu(user: FirebaseUser?) {
        mMenu?.let {
            val visible = user != null
            it.findItem(R.id.item_logout).isVisible = visible
            it.findItem(R.id.item_login).isVisible = !visible
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 101
        const val PERMISSION_REQUEST_CODE = 100
        fun isGPSEnabled(context: Context): Boolean {
            val service: LocationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }
}
