package cz.cvut.zan.stepavi2.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cz.cvut.zan.stepavi2.weatherapp.ui.navigation.WeatherNavGraph
import cz.cvut.zan.stepavi2.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                WeatherNavGraph()
            }
        }
    }
}