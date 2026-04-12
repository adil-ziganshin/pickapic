package com.example.pickapic.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegistrationRoute

@Serializable
object HomeRoute

@Serializable
data class PicturesRoute(val topic: String)

@Serializable
object FavouritePicRoute

@Serializable
data class FullPicRoute(val pictureUrl: String)
