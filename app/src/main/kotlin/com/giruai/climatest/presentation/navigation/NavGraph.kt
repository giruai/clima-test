package com.giruai.climatest.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.giruai.climatest.presentation.components.GlassBottomNav
import com.giruai.climatest.presentation.components.GlassNavItem
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

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Weather : BottomNavItem(Routes.WEATHER, Icons.Filled.Home, "Weather")
    object Search : BottomNavItem(Routes.SEARCH, Icons.Filled.Search, "Search")
    object Favorites : BottomNavItem(Routes.FAVORITES, Icons.Filled.Favorite, "Favorites")
    object Settings : BottomNavItem(Routes.SETTINGS, Icons.Filled.Settings, "Settings")
}

@Composable
fun ClimaNavGraph() {
    val navController = rememberNavController()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val glassNavItems = listOf(
        GlassNavItem(Routes.WEATHER, Icons.Filled.Home, "Weather"),
        GlassNavItem(Routes.SEARCH, Icons.Filled.Search, "Search"),
        GlassNavItem(Routes.FAVORITES, Icons.Filled.Favorite, "Favorites"),
        GlassNavItem(Routes.SETTINGS, Icons.Filled.Settings, "Settings")
    )

    Scaffold(
        bottomBar = {
            GlassBottomNav(
                items = glassNavItems,
                selectedRoute = currentRoute,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.WEATHER) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { paddingValues ->
        ClimaNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ClimaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER,
        modifier = modifier
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
                initialLongitude = longitude
            )
        }

        composable(route = Routes.SEARCH) {
            SearchScreen(
                onCitySelected = { cityId, lat, lon ->
                    navController.navigate(Routes.weatherWithArgs(lat, lon)) {
                        popUpTo(Routes.WEATHER) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Routes.FAVORITES) {
            FavoritesScreen(
                onCitySelected = { cityId, lat, lon ->
                    navController.navigate(Routes.weatherWithArgs(lat, lon))
                }
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen()
        }
    }
}
