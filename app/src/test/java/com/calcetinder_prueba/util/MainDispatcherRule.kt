package com.calcetinder_prueba.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit Rule que reemplaza Dispatchers.Main por un TestDispatcher.
 *
 * Necesario porque viewModelScope usa Dispatchers.Main internamente.
 * Sin esta regla, los tests de ViewModel que lanzan coroutines fallan
 * con "Module with the Main dispatcher had failed to initialize".
 *
 * Uso:
 *   @get:Rule val mainDispatcherRule = MainDispatcherRule()
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
