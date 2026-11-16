package com.razzaaq.productcatalogmvp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.razzaaq.productcatalogmvp.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.my_com_google_android_gms_fonts_certs
)

private val Editorial = FontFamily(
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider)
)
private val Modern = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Editorial,
        fontSize = 40.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineLarge = TextStyle(
        fontFamily = Editorial,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineMedium = TextStyle(
        fontFamily = Modern,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium
    ),
    titleLarge = TextStyle(
        fontFamily = Modern,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontFamily = Modern,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontFamily = Modern,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = Modern,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = Modern,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelSmall = TextStyle(
        fontFamily = Modern,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
)
