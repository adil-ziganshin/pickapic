package com.example.pickapic.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegistrationRoute

@Serializable
data class PicturesRoute(val topic: String)

@Serializable
object HomeRoute

@Serializable
object FavouritePicRoute