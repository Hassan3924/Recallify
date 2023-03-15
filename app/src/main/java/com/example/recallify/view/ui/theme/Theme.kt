package com.example.recallify.view.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = dark_Primary,
    primaryVariant = dark_PrimaryVariant,
    secondary = dark_Secondary,
    secondaryVariant = dark_SecondaryVariant,
    background = dark_Background,
    surface = dark_Surface,
    error = dark_Error,
    onPrimary = dark_onPrimary,
    onSecondary = dark_onSecondary,
    onBackground = dark_onBackground,
    onSurface = dark_onSurface,
    onError = dark_onError
)

private val LightColorPalette = lightColors(
    primary = light_Primary,
    primaryVariant = light_PrimaryVariant,
    secondary = light_Secondary,
    secondaryVariant = light_SecondaryVariant,
    background = light_Background,
    surface = light_Surface,
    error = light_Error,
    onPrimary = light_onPrimary,
    onSecondary = light_onSecondary,
    onBackground = light_onBackground,
    onSurface = light_onSurface,
    onError = light_onError
)

@Composable
fun RecallifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = if (darkTheme) dark_Surface else light_Surface
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}