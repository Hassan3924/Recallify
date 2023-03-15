package com.example.recallify.view.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.recallify.R

private val Poppins = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_medium, weight = FontWeight.Medium),
    Font(R.font.poppins_semibold, weight = FontWeight.SemiBold),
    Font(R.font.poppins_bold, weight = FontWeight.Bold),
    Font(R.font.poppins_extra, weight = FontWeight.ExtraBold),
    Font(R.font.poppins_black, weight = FontWeight.Black),

    )

private val Roboto = FontFamily(
    Font(R.font.roboto_regular),
    Font(R.font.roboto_medium, weight = FontWeight.Medium),
    Font(R.font.roboto_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_black, weight = FontWeight.Black),

    )


val Typography = Typography(
    defaultFontFamily = Poppins,
    h1 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
    ),
    h2 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
    ),
    h3 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
    ),
    h4 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
    ),
    h5 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    h6 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),
    subtitle1 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    subtitle2 = TextStyle(
        fontFamily  = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    body1 = TextStyle(
        fontFamily  = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    body2 = TextStyle(
        fontFamily  = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    button = TextStyle(
        fontFamily  = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    caption = TextStyle(
        fontFamily  = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    overline = TextStyle(
        fontFamily  = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
    )
)