package com.calcetinder_prueba

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de Hilt en la aplicación.
 *
 * @HiltAndroidApp dispara la generación de código de DI en tiempo de compilación
 * y crea el componente raíz de Hilt que vive mientras la app esté viva.
 * Registrar en AndroidManifest.xml con android:name=".CalcetinderApp".
 */
@HiltAndroidApp
class CalcetinderApp : Application()
