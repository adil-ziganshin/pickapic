package com.example.pickapic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pickapic.core.navigation.FavouritePicRoute
import com.example.pickapic.core.navigation.FullPicRoute
import com.example.pickapic.core.navigation.HomeRoute
import com.example.pickapic.core.navigation.LoginRoute
import com.example.pickapic.core.navigation.PicturesRoute
import com.example.pickapic.core.navigation.RegistrationRoute
import com.example.pickapic.feature.auth.LoginScreen
import com.example.pickapic.feature.auth.RegistrationScreen
import com.example.pickapic.feature.favorites.FavouritePicScreen
import com.example.pickapic.feature.home.HomeScreen
import com.example.pickapic.feature.pictures.FullPicScreen
import com.example.pickapic.feature.pictures.PicturesScreenRoute

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(navController = navController)
        }
        composable<RegistrationRoute> {
            RegistrationScreen(navController = navController)
        }
        composable<HomeRoute> {
            HomeScreen(navController = navController)
        }
        composable<PicturesRoute> {
            PicturesScreenRoute()
        }
        composable<FavouritePicRoute> {
            FavouritePicScreen()
        }
        composable<FullPicRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<FullPicRoute>()
            FullPicScreen(pictureUrl = route.pictureUrl)
        }
    }
}
