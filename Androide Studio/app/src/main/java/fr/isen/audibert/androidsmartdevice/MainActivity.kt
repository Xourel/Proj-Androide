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
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.audibert.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Active le mode immersif (bord à bord) pour une meilleure expérience visuelle
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContentComponent(
                        innerPadding = innerPadding,
                        onButtonClick = { this.goToScan() }
                    )
                }
            }
        }
    }

    private fun goToScan() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun MainContentComponent(innerPadding: PaddingValues, onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        //Titre
        Text(
            text = "Android Smart Device",
            fontSize = 22.sp,
            textAlign = Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Cette application permet de scanner des appareils BLE à proximité.",
                textAlign = Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                modifier = Modifier
                    .size(80.dp)
                    .padding(vertical = 8.dp),
                painter = painterResource(R.drawable.bluetooth_icon),
                contentDescription = "logo"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 30.dp),
            onClick = onButtonClick
        )
        {
            Text(text = "Scan BLE")
        }
    }
}