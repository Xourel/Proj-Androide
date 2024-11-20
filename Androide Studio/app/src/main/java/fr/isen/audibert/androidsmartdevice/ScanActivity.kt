package fr.isen.audibert.androidsmartdevice

import fr.isen.audibert.androidsmartdevice.composable.ScanContentComponent
import fr.isen.audibert.androidsmartdevice.composable.SettingsFlags
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme


class ScanActivity : ComponentActivity() {

    private lateinit var flags: SettingsFlags
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                scanLeDevice()
            } else {
                showPermissionDeniedMessage()
            }
        }



    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val scanning = mutableStateOf<Boolean>(false)
        val scanList = mutableStateListOf<ScanResult>() // Initialise ScanList
        flags = SettingsFlags(
            ErrorMessage = "",
            Error = false,
            Scanning = scanning,
            ScanList = scanList
        )
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScanContentComponent(
                        innerPadding = innerPadding,
                        flags = flags,
                        onToggleScan = {
                            flags.Scanning.value = !flags.Scanning.value
                            scanLeDevice()
                        }
                    )
                }
            }
        }
        checkConnectivity(flags)
        initBLE()

    }

    private fun showPermissionDeniedMessage() {
        Log.d("PermissionsDebug", "Toutes les permissions ont été accordées.")
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            flags.ScanList.add(result)
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        handler = Handler(mainLooper)
        if (flags.Scanning.value) {
            // Démarrer le scan
            handler.postDelayed({
                flags.Scanning.value = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            // Arrêter le scan
            bluetoothLeScanner?.stopScan(leScanCallback)
            flags.Scanning.value = false
        }
    }


    private fun getAllPermissionsForBLE(): Array<String> {
        var allPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            allPermissions = allPermissions.plus(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            allPermissions = allPermissions.plus(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return allPermissions
    }

    private fun initBLE() {
        if (bluetoothAdapter?.isEnabled == true) {
            scanLeDeviceWithPermission()
        }
    }

    private fun scanLeDeviceWithPermission() {
        if (allPermissionGranted()) {
            Log.d("PermissionsDebug", "Toutes les permissions ont été accordées.")
            scanLeDevice()
        } else {
            Log.d("PermissionsDebug", "Certaines permissions sont manquantes.")
            requestPermissionLauncher.launch(getAllPermission())
        }
    }

    private fun allPermissionGranted(): Boolean {
        val allPermission = getAllPermission()
        return allPermission.all { permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getAllPermission(): Array<String> {
        val allPermissions = getAllPermissionsForBLE()
        return allPermissions
    }

    // 1. Définition des flags
    private fun checkConnectivity(flags: SettingsFlags) {
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            flags.ErrorMessage = "Le bluetooth ne marche pas sur cet appareil"
        } else {
            if (!bluetoothAdapter!!.isEnabled) {
                flags.ErrorMessage = "Activer le Bluetooth"
            }
        }
    }

}




