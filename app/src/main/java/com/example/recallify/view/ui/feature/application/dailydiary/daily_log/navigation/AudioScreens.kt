package com.example.speech_to_text_jetpack.navigation

enum class AudioScreens {
    HomeScreen,
    AudioLogScreen,
    LoginScreen;

    companion object {
        fun fromRoute(route : String?) : AudioScreens
        = when (route?.substringBefore("/")) {

            HomeScreen.name -> HomeScreen
            AudioLogScreen.name -> AudioLogScreen
            LoginScreen.name -> LoginScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")

        }
    }
}