package com.example.pickapic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pickapic.core.navigation.FavoritePicRoute
import com.example.pickapic.core.navigation.HomeRoute
import com.example.pickapic.core.navigation.LoginRoute
import com.example.pickapic.core.navigation.PicturesRoute
import com.example.pickapic.core.navigation.RegistrationRoute
import com.example.pickapic.feature.auth.LoginScreen
import com.example.pickapic.feature.auth.RegistrationScreen
import com.example.pickapic.feature.favorites.FavoritePicScreen
import com.example.pickapic.feature.home.HomeScreen
import com.example.pickapic.feature.pictures.PicturesScreenRoute

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<LoginRoute> {
            LoginScreen(navController = navController)
        }
        composable<RegistrationRoute> {
            RegistrationScreen(navController = navController)
        }
        composable<HomeRoute> {
            HomeScreen(
                onPerformSearch = {
                    navController.navigate(PicturesRoute(topic = it))
                },
                onFavoriteButtonClick = {
                    navController.navigate(FavoritePicRoute)
                }
            )
        }
        composable<PicturesRoute> {
            PicturesScreenRoute()
        }
        composable<FavoritePicRoute> {
            FavoritePicScreen()
        }
    }
}