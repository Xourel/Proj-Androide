package fr.isen.audibert.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ConnectionActivity : ComponentActivity() {
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var ledCharacteristic: BluetoothGattCharacteristic? = null
    private var isConnected by mutableStateOf(false)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deviceName = intent.getStringExtra("deviceName")
        val deviceAddress = intent.getStringExtra("deviceAddress")

        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
        bluetoothGatt = bluetoothDevice?.connectGatt(this, false, bluetoothGattCallback)

        setContent {
            ConnectionScreen(
                deviceName = deviceName,
                deviceAddress = deviceAddress,
                isConnected = isConnected,
                writeLed = { ledNumber ->
                    writeLedValue(
                        bluetoothGatt,
                        ledCharacteristic,
                        ledNumber
                    )
                },
                disconect = { Disconect(bluetoothGatt!!) }
            )
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE", "Connection avec le serveur")
                isConnected = true
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Déconnexion du serveur")
                isConnected = false
                gatt.connect()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services trouvées")
                val service = gatt.services
                ledCharacteristic = service?.get(2)?.getCharacteristics()?.get(0)
                Log.d("BLE", "Characteristic trouvée")
            } else {
                Log.d("BLE", "Erreur lors de la découverte des services")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Characteristic mise a jour: ${characteristic?.uuid}")
            } else {
                Log.d(
                    "BLE",
                    "Erreur dans la mise a jour de la characteristic : ${characteristic?.uuid}"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun writeLedValue(
        bluetoothGatt: BluetoothGatt?,
        ledCharacteristic: BluetoothGattCharacteristic?,
        ledNumber: Int
    ) {
        ledCharacteristic?.let { characteristic ->
            val value = when (ledNumber) {
                1 -> byteArrayOf(0x01)
                2 -> byteArrayOf(0x02)
                3 -> byteArrayOf(0x03)
                else -> byteArrayOf(0x00)
            }
            characteristic.value = value
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }

    @SuppressLint("MissingPermission")
    fun Disconect(bluetoothGatt: BluetoothGatt) {
        bluetoothGatt.disconnect()
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun ConnectionScreen(
    deviceName: String?,
    deviceAddress: String?,
    isConnected: Boolean,
    writeLed: (Int) -> Unit,
    disconect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête avec le nom et l'adresse de l'appareil
        Text(
            text = "Connexion à $deviceName",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Adresse : $deviceAddress",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Message de connexion
        if (!isConnected) {
            Text(
                text = "Connexion en cours...",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            /*
            Button(onClick = {
                isConnected = true // Simule l'état de connexion
            }) {
                Text("Simuler une connexion ")
            }
            */
        } else {
            // Interface après connexion réussie
            InteractionOptions(writeLed)
        }
        Button(
            onClick = { disconect() },
            modifier = Modifier.size(65.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Déconection", fontSize = 10.sp)
        }
    }
}

@Composable
fun InteractionOptions(writeLed: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section LEDs
        Text(
            "Contrôle des LEDs",
            fontSize = 20.sp,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LEDControlButton(
                    ledNumber = 1,
                    onLedClick = { writeLed(1) })
                LEDControlButton(
                    ledNumber = 2,
                    onLedClick = { writeLed(2) })
                LEDControlButton(
                    ledNumber = 3,
                    onLedClick = { writeLed(3) })
            }
            Button(
                onClick = { writeLed(0) },
                Modifier.fillMaxWidth(0.8f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Eteindre les LED", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun LEDControlButton(ledNumber: Int, onLedClick: () -> Unit) {
    Button(
        onClick = onLedClick,
        modifier = Modifier.size(70.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("LED $ledNumber", fontSize = 10.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {

    }
}