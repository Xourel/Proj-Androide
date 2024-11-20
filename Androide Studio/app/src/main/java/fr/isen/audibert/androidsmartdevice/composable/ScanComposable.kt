package fr.isen.audibert.androidsmartdevice.composable

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.ConnectionActivity
import fr.isen.audibert.androidsmartdevice.R


class SettingsFlags(
    var ErrorMessage: String,
    var Error: Boolean,
    var Scanning: MutableState<Boolean>,
    var ScanList: MutableList<ScanResult>
)


@SuppressLint("MissingPermission")
@Composable
fun ScanContentComponent(
    innerPadding: PaddingValues,
    flags: SettingsFlags,
    onToggleScan: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Liste des scans BLE",
            modifier = Modifier.padding(innerPadding),
            fontSize = 40.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center // Centre le contenu de la Box horizontalement
                ) {
                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (!flags.Scanning.value) "Commencer le scan" else "Scan en cours")
                        ElevatedButton(
                            onClick = {
                                onToggleScan()
                            }
                        ) {
                            if (!flags.Scanning.value) {
                                Icon(
                                    painter = painterResource(R.drawable.start),
                                    contentDescription = "Start logo",
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.stop),
                                    contentDescription = "Stop logo",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
                if (flags.Scanning.value) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

        }
        if (flags.ScanList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espace entre les éléments
            ) {
                items(flags.ScanList) { result ->
                    val signalStrength = result.rssi
                    val deviceName = result.device.name ?: "Appareil inconnu"
                    val deviceAddress = result.device.address
                    //if (deviceName != "Appareil inconnu") {
                    DeviceItem(
                        deviceName = deviceName,
                        deviceAddress = deviceAddress,
                        signalStrength = signalStrength,
                        onConnectClick = {

                            val intent = Intent(context, ConnectionActivity::class.java).apply {
                                putExtra("deviceName", deviceName)
                                putExtra("deviceAddress", deviceAddress)
                            }
                            context.startActivity(intent)
                        }
                    )
                    //}
                }
            }
        } else {
            // Affiche un message si la liste est vide ou null, sinon laissez-le vide
            Text(
                text = "Aucun appareil trouvé",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DeviceItem(
    deviceName: String,
    deviceAddress: String,
    signalStrength: Int,
    onConnectClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Signal Strength Circle
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = getSignalColor(signalStrength),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = signalStrength.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Device Info Column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = deviceName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = deviceAddress,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "No Services", // Placeholder for services
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Connect Button
        Button(
            onClick = { onConnectClick() },
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(40.dp)
        ) {
            Text(text = "CONNECT", fontWeight = FontWeight.Bold)
        }
    }
}

fun getSignalColor(signalStrength: Int): Color {
    return when {
        signalStrength > -50 -> Color.Green
        signalStrength > -70 -> Color.Yellow
        else -> Color.Red
    }
}

/*
@Composable
fun DeviceItem(
    deviceName: String,
    deviceAddress: String,
    signalStrength: Int,
    signalColor: Color,
    uuidList: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .background(signalColor.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Text(
            text = "Nom : $deviceName",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Adresse : $deviceAddress", fontSize = 14.sp, color = Color.Gray)
        Text(text = "Signal : $signalStrength dBm", fontSize = 14.sp, color = Color.Gray)

        // Affichage des UUIDs
        if (uuidList.isNotEmpty()) {
            Text(
                text = "UUID(s) : ${uuidList.joinToString(", ")}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        } else {
            Text(
                text = "Aucun UUID trouvé",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
*/
/*
@SuppressLint("MissingPermission")
fun getDeviceUuid(scanResult: ScanResult): List<String> {
    // Extraire les UUID des services de l'appareil
    val uuids = scanResult.scanRecord?.serviceUuids
    return uuids?.map { it.toString() } ?: emptyList()
}*/

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidSmartDeviceTheme {
        fr.isen.audibert.androidsmartdevice.composable.ScanContentComponent(
            innerPadding = PaddingValues(0.dp),
            bleList = listOf("Alice", "Bob", "Charlie", "David", "Eva"),

        )
    }
}*/