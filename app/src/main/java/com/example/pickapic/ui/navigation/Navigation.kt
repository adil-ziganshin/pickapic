package com.example.pickapic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pickapic.ui.screens.FavouritePicScreen
import com.example.pickapic.ui.screens.FullPicScreen
import com.example.pickapic.ui.screens.HomeScreen
import com.example.pickapic.ui.screens.LoginScreen
import com.example.pickapic.ui.screens.PicturesScreen
import com.example.pickapic.ui.screens.RegistrationScreen

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
        composable<PicturesRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PicturesRoute>()
            PicturesScreen(topic = route.topic, navController = navController)
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
