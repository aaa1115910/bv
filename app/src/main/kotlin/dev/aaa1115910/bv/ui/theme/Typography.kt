package dev.aaa1115910.bv.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.Typography
import dev.aaa1115910.bv.R

private val notoMathFont = Font(R.font.noto_sans_math_regular, FontWeight.Normal)
private val notoFontFamily = FontFamily(notoMathFont)

private val dummyTypography = Typography()

val android6AndBelowTypographyTv = Typography(
    displayLarge = dummyTypography.displayLarge.copy(fontFamily = notoFontFamily),
    displayMedium = dummyTypography.displayMedium.copy(fontFamily = notoFontFamily),
    displaySmall = dummyTypography.displaySmall.copy(fontFamily = notoFontFamily),
    headlineLarge = dummyTypography.headlineLarge.copy(fontFamily = notoFontFamily),
    headlineMedium = dummyTypography.headlineMedium.copy(fontFamily = notoFontFamily),
    headlineSmall = dummyTypography.headlineSmall.copy(fontFamily = notoFontFamily),
    titleLarge = dummyTypography.titleLarge.copy(fontFamily = notoFontFamily),
    titleMedium = dummyTypography.titleMedium.copy(fontFamily = notoFontFamily),
    titleSmall = dummyTypography.titleSmall.copy(fontFamily = notoFontFamily),
    bodyLarge = dummyTypography.bodyLarge.copy(fontFamily = notoFontFamily),
    bodyMedium = dummyTypography.bodyMedium.copy(fontFamily = notoFontFamily),
    bodySmall = dummyTypography.bodySmall.copy(fontFamily = notoFontFamily),
    labelLarge = dummyTypography.labelLarge.copy(fontFamily = notoFontFamily),
    labelMedium = dummyTypography.labelMedium.copy(fontFamily = notoFontFamily),
    labelSmall = dummyTypography.labelSmall.copy(fontFamily = notoFontFamily)
)

val android6AndBelowTypographyCommon = androidx.compose.material3.Typography(
    displayLarge = dummyTypography.displayLarge.copy(fontFamily = notoFontFamily),
    displayMedium = dummyTypography.displayMedium.copy(fontFamily = notoFontFamily),
    displaySmall = dummyTypography.displaySmall.copy(fontFamily = notoFontFamily),
    headlineLarge = dummyTypography.headlineLarge.copy(fontFamily = notoFontFamily),
    headlineMedium = dummyTypography.headlineMedium.copy(fontFamily = notoFontFamily),
    headlineSmall = dummyTypography.headlineSmall.copy(fontFamily = notoFontFamily),
    titleLarge = dummyTypography.titleLarge.copy(fontFamily = notoFontFamily),
    titleMedium = dummyTypography.titleMedium.copy(fontFamily = notoFontFamily),
    titleSmall = dummyTypography.titleSmall.copy(fontFamily = notoFontFamily),
    bodyLarge = dummyTypography.bodyLarge.copy(fontFamily = notoFontFamily),
    bodyMedium = dummyTypography.bodyMedium.copy(fontFamily = notoFontFamily),
    bodySmall = dummyTypography.bodySmall.copy(fontFamily = notoFontFamily),
    labelLarge = dummyTypography.labelLarge.copy(fontFamily = notoFontFamily),
    labelMedium = dummyTypography.labelMedium.copy(fontFamily = notoFontFamily),
    labelSmall = dummyTypography.labelSmall.copy(fontFamily = notoFontFamily)
)