import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.R
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

@Composable
fun ScanContentComponent(innerPadding: PaddingValues, bleList: List<String>? = null) {
    val expanded = remember { mutableStateOf(true) }
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
                        Text(if (expanded.value) "Commencer le scan" else "Scan en cours")
                        ElevatedButton(
                            onClick = { expanded.value = !expanded.value }
                        ) {
                            if (expanded.value) {
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
                if (!expanded.value) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

        }
        if (!bleList.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espace entre les éléments
            ) {
                items(bleList) { name ->
                    Text(text = name)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidSmartDeviceTheme {
        ScanContentComponent(
            innerPadding = PaddingValues(0.dp),
            bleList = listOf("Alice", "Bob", "Charlie", "David", "Eva")
        )
    }
}