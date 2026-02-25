package com.giruai.climatest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.giruai.climatest.presentation.screen.favorites.FavoritesScreen
import com.giruai.climatest.presentation.screen.search.SearchScreen
import com.giruai.climatest.presentation.screen.settings.SettingsScreen
import com.giruai.climatest.presentation.screen.weather.WeatherScreen

object Routes {
    const val WEATHER = "weather"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"

    fun weatherWithArgs(lat: Double? = null, lon: Double? = null): String {
        return if (lat != null && lon != null) {
            "$WEATHER?lat=$lat&lon=$lon"
        } else {
            WEATHER
        }
    }
}

@Composable
fun ClimaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER
    ) {
        composable(
            route = "${Routes.WEATHER}?lat={lat}&lon={lon}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("lon") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val latString = backStackEntry.arguments?.getString("lat")
            val lonString = backStackEntry.arguments?.getString("lon")
            val latitude = latString?.toDoubleOrNull()
            val longitude = lonString?.toDoubleOrNull()

            WeatherScreen(
                initialLatitude = latitude,
                initialLongitude = longitude,
                onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(route = Routes.SEARCH) {
            SearchScreen(
                onCitySelected = { cityId, lat, lon ->
                    navController.navigate(Routes.weatherWithArgs(lat, lon)) {
                        popUpTo(Routes.WEATHER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Routes.FAVORITES) {
            FavoritesScreen(
                onCitySelected = { cityId, lat, lon ->
                    navController.navigate(Routes.weatherWithArgs(lat, lon))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
