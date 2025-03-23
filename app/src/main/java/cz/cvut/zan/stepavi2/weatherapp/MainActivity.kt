package cz.cvut.zan.stepavi2.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import cz.cvut.zan.stepavi2.weatherapp.ui.navigation.WeatherNavGraph
import cz.cvut.zan.stepavi2.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                var navController by remember { mutableStateOf<NavController?>(null) }

                WeatherNavGraph { controller ->
                    navController = controller
                }

                navController?.let { controller ->
                    HandleNotificationIntent(controller)
                }
            }
        }
    }

    @Composable
    private fun HandleNotificationIntent(navController: NavController) {
        val city = intent.getStringExtra("city")
        val navigateToDetails = intent.getBooleanExtra("navigate_to_details", false)

        if (navigateToDetails && city != null) {
            LaunchedEffect(Unit) {
                navController.navigate("detail/$city") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
                intent.removeExtra("city")
                intent.removeExtra("navigate_to_details")
            }
        }
    }
}