package com.kontranik.koreader.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import de.kontranik.freebudget.ui.theme.backgroundDark
import de.kontranik.freebudget.ui.theme.backgroundDarkHighContrast
import de.kontranik.freebudget.ui.theme.backgroundDarkMediumContrast
import de.kontranik.freebudget.ui.theme.backgroundLight
import de.kontranik.freebudget.ui.theme.backgroundLightHighContrast
import de.kontranik.freebudget.ui.theme.backgroundLightMediumContrast
import de.kontranik.freebudget.ui.theme.errorContainerDark
import de.kontranik.freebudget.ui.theme.errorContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.errorContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.errorContainerLight
import de.kontranik.freebudget.ui.theme.errorContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.errorContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.errorDark
import de.kontranik.freebudget.ui.theme.errorDarkHighContrast
import de.kontranik.freebudget.ui.theme.errorDarkMediumContrast
import de.kontranik.freebudget.ui.theme.errorLight
import de.kontranik.freebudget.ui.theme.errorLightHighContrast
import de.kontranik.freebudget.ui.theme.errorLightMediumContrast
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceDark
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceDarkHighContrast
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceDarkMediumContrast
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceLight
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceLightHighContrast
import de.kontranik.freebudget.ui.theme.inverseOnSurfaceLightMediumContrast
import de.kontranik.freebudget.ui.theme.inversePrimaryDark
import de.kontranik.freebudget.ui.theme.inversePrimaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.inversePrimaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.inversePrimaryLight
import de.kontranik.freebudget.ui.theme.inversePrimaryLightHighContrast
import de.kontranik.freebudget.ui.theme.inversePrimaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.inverseSurfaceDark
import de.kontranik.freebudget.ui.theme.inverseSurfaceDarkHighContrast
import de.kontranik.freebudget.ui.theme.inverseSurfaceDarkMediumContrast
import de.kontranik.freebudget.ui.theme.inverseSurfaceLight
import de.kontranik.freebudget.ui.theme.inverseSurfaceLightHighContrast
import de.kontranik.freebudget.ui.theme.inverseSurfaceLightMediumContrast
import de.kontranik.freebudget.ui.theme.onBackgroundDark
import de.kontranik.freebudget.ui.theme.onBackgroundDarkHighContrast
import de.kontranik.freebudget.ui.theme.onBackgroundDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onBackgroundLight
import de.kontranik.freebudget.ui.theme.onBackgroundLightHighContrast
import de.kontranik.freebudget.ui.theme.onBackgroundLightMediumContrast
import de.kontranik.freebudget.ui.theme.onErrorContainerDark
import de.kontranik.freebudget.ui.theme.onErrorContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.onErrorContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onErrorContainerLight
import de.kontranik.freebudget.ui.theme.onErrorContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.onErrorContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.onErrorDark
import de.kontranik.freebudget.ui.theme.onErrorDarkHighContrast
import de.kontranik.freebudget.ui.theme.onErrorDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onErrorLight
import de.kontranik.freebudget.ui.theme.onErrorLightHighContrast
import de.kontranik.freebudget.ui.theme.onErrorLightMediumContrast
import de.kontranik.freebudget.ui.theme.onPrimaryContainerDark
import de.kontranik.freebudget.ui.theme.onPrimaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.onPrimaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onPrimaryContainerLight
import de.kontranik.freebudget.ui.theme.onPrimaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.onPrimaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.onPrimaryDark
import de.kontranik.freebudget.ui.theme.onPrimaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.onPrimaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onPrimaryLight
import de.kontranik.freebudget.ui.theme.onPrimaryLightHighContrast
import de.kontranik.freebudget.ui.theme.onPrimaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.onSecondaryContainerDark
import de.kontranik.freebudget.ui.theme.onSecondaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.onSecondaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onSecondaryContainerLight
import de.kontranik.freebudget.ui.theme.onSecondaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.onSecondaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.onSecondaryDark
import de.kontranik.freebudget.ui.theme.onSecondaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.onSecondaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onSecondaryLight
import de.kontranik.freebudget.ui.theme.onSecondaryLightHighContrast
import de.kontranik.freebudget.ui.theme.onSecondaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.onSurfaceDark
import de.kontranik.freebudget.ui.theme.onSurfaceDarkHighContrast
import de.kontranik.freebudget.ui.theme.onSurfaceDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onSurfaceLight
import de.kontranik.freebudget.ui.theme.onSurfaceLightHighContrast
import de.kontranik.freebudget.ui.theme.onSurfaceLightMediumContrast
import de.kontranik.freebudget.ui.theme.onSurfaceVariantDark
import de.kontranik.freebudget.ui.theme.onSurfaceVariantDarkHighContrast
import de.kontranik.freebudget.ui.theme.onSurfaceVariantDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onSurfaceVariantLight
import de.kontranik.freebudget.ui.theme.onSurfaceVariantLightHighContrast
import de.kontranik.freebudget.ui.theme.onSurfaceVariantLightMediumContrast
import de.kontranik.freebudget.ui.theme.onTertiaryContainerDark
import de.kontranik.freebudget.ui.theme.onTertiaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.onTertiaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onTertiaryContainerLight
import de.kontranik.freebudget.ui.theme.onTertiaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.onTertiaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.onTertiaryDark
import de.kontranik.freebudget.ui.theme.onTertiaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.onTertiaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.onTertiaryLight
import de.kontranik.freebudget.ui.theme.onTertiaryLightHighContrast
import de.kontranik.freebudget.ui.theme.onTertiaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.outlineDark
import de.kontranik.freebudget.ui.theme.outlineDarkHighContrast
import de.kontranik.freebudget.ui.theme.outlineDarkMediumContrast
import de.kontranik.freebudget.ui.theme.outlineLight
import de.kontranik.freebudget.ui.theme.outlineLightHighContrast
import de.kontranik.freebudget.ui.theme.outlineLightMediumContrast
import de.kontranik.freebudget.ui.theme.outlineVariantDark
import de.kontranik.freebudget.ui.theme.outlineVariantDarkHighContrast
import de.kontranik.freebudget.ui.theme.outlineVariantDarkMediumContrast
import de.kontranik.freebudget.ui.theme.outlineVariantLight
import de.kontranik.freebudget.ui.theme.outlineVariantLightHighContrast
import de.kontranik.freebudget.ui.theme.outlineVariantLightMediumContrast
import de.kontranik.freebudget.ui.theme.primaryContainerDark
import de.kontranik.freebudget.ui.theme.primaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.primaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.primaryContainerLight
import de.kontranik.freebudget.ui.theme.primaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.primaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.primaryDark
import de.kontranik.freebudget.ui.theme.primaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.primaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.primaryLight
import de.kontranik.freebudget.ui.theme.primaryLightHighContrast
import de.kontranik.freebudget.ui.theme.primaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.scrimDark
import de.kontranik.freebudget.ui.theme.scrimDarkHighContrast
import de.kontranik.freebudget.ui.theme.scrimDarkMediumContrast
import de.kontranik.freebudget.ui.theme.scrimLight
import de.kontranik.freebudget.ui.theme.scrimLightHighContrast
import de.kontranik.freebudget.ui.theme.scrimLightMediumContrast
import de.kontranik.freebudget.ui.theme.secondaryContainerDark
import de.kontranik.freebudget.ui.theme.secondaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.secondaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.secondaryContainerLight
import de.kontranik.freebudget.ui.theme.secondaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.secondaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.secondaryDark
import de.kontranik.freebudget.ui.theme.secondaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.secondaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.secondaryLight
import de.kontranik.freebudget.ui.theme.secondaryLightHighContrast
import de.kontranik.freebudget.ui.theme.secondaryLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceBrightDark
import de.kontranik.freebudget.ui.theme.surfaceBrightDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceBrightDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceBrightLight
import de.kontranik.freebudget.ui.theme.surfaceBrightLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceBrightLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerDark
import de.kontranik.freebudget.ui.theme.surfaceContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighDark
import de.kontranik.freebudget.ui.theme.surfaceContainerHighDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighLight
import de.kontranik.freebudget.ui.theme.surfaceContainerHighLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestDark
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestLight
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerHighestLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLight
import de.kontranik.freebudget.ui.theme.surfaceContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowDark
import de.kontranik.freebudget.ui.theme.surfaceContainerLowDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowLight
import de.kontranik.freebudget.ui.theme.surfaceContainerLowLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestDark
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestLight
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceContainerLowestLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceDark
import de.kontranik.freebudget.ui.theme.surfaceDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceDimDark
import de.kontranik.freebudget.ui.theme.surfaceDimDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceDimDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceDimLight
import de.kontranik.freebudget.ui.theme.surfaceDimLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceDimLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceLight
import de.kontranik.freebudget.ui.theme.surfaceLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceLightMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceVariantDark
import de.kontranik.freebudget.ui.theme.surfaceVariantDarkHighContrast
import de.kontranik.freebudget.ui.theme.surfaceVariantDarkMediumContrast
import de.kontranik.freebudget.ui.theme.surfaceVariantLight
import de.kontranik.freebudget.ui.theme.surfaceVariantLightHighContrast
import de.kontranik.freebudget.ui.theme.surfaceVariantLightMediumContrast
import de.kontranik.freebudget.ui.theme.tertiaryContainerDark
import de.kontranik.freebudget.ui.theme.tertiaryContainerDarkHighContrast
import de.kontranik.freebudget.ui.theme.tertiaryContainerDarkMediumContrast
import de.kontranik.freebudget.ui.theme.tertiaryContainerLight
import de.kontranik.freebudget.ui.theme.tertiaryContainerLightHighContrast
import de.kontranik.freebudget.ui.theme.tertiaryContainerLightMediumContrast
import de.kontranik.freebudget.ui.theme.tertiaryDark
import de.kontranik.freebudget.ui.theme.tertiaryDarkHighContrast
import de.kontranik.freebudget.ui.theme.tertiaryDarkMediumContrast
import de.kontranik.freebudget.ui.theme.tertiaryLight
import de.kontranik.freebudget.ui.theme.tertiaryLightHighContrast
import de.kontranik.freebudget.ui.theme.tertiaryLightMediumContrast


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        //typography = Typography,
        content = content
    )
}

