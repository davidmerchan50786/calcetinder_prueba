package com.calcetinder_prueba.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CalcetinderColorScheme = lightColorScheme(
    primary          = CalcetinderPink,
    onPrimary        = CalcetinderWhite,
    primaryContainer = CalcetinderPinkLight,
    secondary        = CalcetinderPinkDark,
    onSecondary      = CalcetinderWhite,
    background       = CalcetinderWhite,
    surface          = CalcetinderWhite,
    onBackground     = CalcetinderDark,
    onSurface        = CalcetinderDark,
)

@Composable
fun CalcetinderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CalcetinderColorScheme,
        typography  = CalcetinderTypography,
        content     = content
    )
}
