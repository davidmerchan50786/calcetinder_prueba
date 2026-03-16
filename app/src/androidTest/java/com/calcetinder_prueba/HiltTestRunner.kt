package com.calcetinder_prueba

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner que sustituye la Application por HiltTestApplication.
 *
 * Necesario para que @HiltAndroidTest pueda:
 *  - Reemplazar el grafo Hilt real por uno de prueba
 *  - Inyectar fakes/mocks via @UninstallModules + @BindValue
 *
 * Registrado como testInstrumentationRunner en app/build.gradle.kts.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
