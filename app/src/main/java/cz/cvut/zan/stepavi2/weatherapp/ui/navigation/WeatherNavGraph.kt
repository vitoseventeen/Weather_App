package cz.cvut.zan.stepavi2.weatherapp.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail.DetailScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites.FavoritesScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast.ForecastScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.home.HomeScreen
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings.SettingsScreen
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun WeatherNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/{")

    Scaffold(
        bottomBar = {
            WeatherBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    onCityClick = { city ->
                        navController.navigate("detail/$city") {
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    paddingValues = paddingValues
                )
            }
            composable("detail/{city}") { backStackEntry ->
                val city = backStackEntry.arguments?.getString("city") ?: ""
                DetailScreen(
                    city = city,
                    paddingValues = paddingValues,
                    navController = navController
                )
            }
            composable("forecast") {
                ForecastScreen(
                    paddingValues = paddingValues
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onCityClick = { city ->
                        navController.navigate("detail/$city") {
                            popUpTo("favorites") { inclusive = false }
                        }
                    },
                    paddingValues = paddingValues
                )
            }
            composable("settings") {
                SettingsScreen(
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun WeatherBottomNavigation(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem(stringResource(R.string.home), "home", R.drawable.ic_home),
        BottomNavItem(stringResource(R.string.forecast_nav), "forecast", R.drawable.ic_forecast),
        BottomNavItem(stringResource(R.string.favorites_nav), "favorites", R.drawable.ic_favorite),
        BottomNavItem(stringResource(R.string.settings_nav), "settings", R.drawable.ic_settings)
    )

    NavigationBar(
        modifier = Modifier.height(Dimens.BottomNavHeight),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(Dimens.IconSizeSmall),
                        tint = if (currentRoute == item.route)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        item.title,
                        fontSize = Dimens.TextSizeSmall,
                        color = if (currentRoute == item.route)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            popUpTo(item.route) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else if (currentRoute == "detail") {
                        navController.popBackStack(item.route, inclusive = false)
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Int
)