package cz.cvut.zan.stepavi2.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail.DetailScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites.FavoritesScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.home.HomeScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.search.SearchScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings.SettingsScreen

@Composable
fun WeatherNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCityClick = { city -> navController.navigate("detail/$city") }
            )
        }
        composable("detail/{city}") { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            DetailScreen(city = city)
        }
        composable("search") {
            SearchScreen(
                onCitySelected = { city -> navController.navigate("detail/$city") }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                onCityClick = { city -> navController.navigate("detail/$city") }
            )
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}