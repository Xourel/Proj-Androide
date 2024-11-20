package fr.isen.audibert.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ConnectionActivity : ComponentActivity() {
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var ledCharacteristic: BluetoothGattCharacteristic? = null
    private var isConnected by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deviceName = intent.getStringExtra("deviceName")
        val deviceAddress = intent.getStringExtra("deviceAddress")

        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)

        setContent {
            ConnectionScreen(
                deviceName = deviceName,
                deviceAddress = deviceAddress,
                onConnect = ::Connect
            )
        }
    }
    private fun Connect() {
        //bluetoothGatt = bluetoothDevice?.connectGatt(this, false, gattCallback)
    }
}

@Composable
fun ConnectionScreen(deviceName: String?, deviceAddress: String?, onConnect: () -> Unit) {
    var isConnected by remember { mutableStateOf(false) } // Gère l'état de la connexion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête avec le nom et l'adresse de l'appareil
        Text(text = "Connexion à $deviceName", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
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
            Button(onClick = {
                onConnect()
                isConnected = true // Simule l'état de connexion
            }) {
                Text("Connecter")
            }
        } else {
            // Interface après connexion réussie
            InteractionOptions()
        }
    }
}

@Composable
fun InteractionOptions() {
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
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LEDControlButton(
                ledNumber = 1,
                onLedClick = { /* Écrire dans la caractéristique LED1 */ })
            LEDControlButton(
                ledNumber = 2,
                onLedClick = { /* Écrire dans la caractéristique LED2 */ })
            LEDControlButton(
                ledNumber = 3,
                onLedClick = { /* Écrire dans la caractéristique LED3 */ })
        }
    }
}

@Composable
fun LEDControlButton(ledNumber: Int, onLedClick: () -> Unit) {
    Button(
        onClick = onLedClick,
        modifier = Modifier.size(80.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("LED $ledNumber", fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {

    }
}