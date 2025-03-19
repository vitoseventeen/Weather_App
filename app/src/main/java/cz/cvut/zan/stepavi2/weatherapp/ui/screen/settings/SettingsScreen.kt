package cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var unit by remember { mutableStateOf("Celsius") }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Temperature Unit",
                modifier = Modifier.padding(top = 16.dp)
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unit == "Celsius",
                        onClick = { unit = "Celsius" }
                    )
                    Text("Celsius")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unit == "Fahrenheit",
                        onClick = { unit = "Fahrenheit" }
                    )
                    Text("Fahrenheit")
                }
            }
        }
    }
}