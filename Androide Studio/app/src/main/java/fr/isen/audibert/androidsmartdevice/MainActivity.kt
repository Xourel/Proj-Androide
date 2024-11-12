package fr.isen.audibert.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    MainContentComponent(
                        innerPadding = innerPadding,
                        onButtonClick = {
                            val intent = Intent(this, ScanActivity::class.java)
                            startActivity(intent)
                        })
                }
            }
        }
    }
}

@Composable
fun MainContentComponent(innerPadding: PaddingValues, onButtonClick: () -> Unit){
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp,12.dp),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = "Bienvenue sur AndroidSmartDevice",
                modifier = Modifier.padding(innerPadding),
                fontSize = 40.sp ,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
            Text(text = "pour démarer le scan des devices BLE, cliquer sur le bouton")
            Image(
                painter = painterResource(R.drawable.bluetooth_icon_670069_1280),
                contentDescription = "Bluetooth logo",
                modifier = Modifier.size(100.dp)
                                    .padding(0.dp,16.dp)
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onButtonClick) {
            Text("Valider")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {

    }
}