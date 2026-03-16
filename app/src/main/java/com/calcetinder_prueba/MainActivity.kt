package com.calcetinder_prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.calcetinder_prueba.navigation.AppNavigation
import com.calcetinder_prueba.ui.theme.CalcetinderTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint permite que Hilt inyecte dependencias en esta Activity
// y en todos los Composables dentro de ella vía hiltViewModel()
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalcetinderTheme {
                AppNavigation()
            }
        }
    }
}