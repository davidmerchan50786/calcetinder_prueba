package com.calcetinder_prueba.di

import com.calcetinder_prueba.util.FaceDetector
import com.calcetinder_prueba.util.MlKitFaceDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que vincula la interfaz FaceDetector con su implementación de producción.
 *
 * Los tests instrumentados pueden reemplazar este módulo via:
 *   @UninstallModules(DetectionModule::class)
 *   @HiltAndroidTest
 *   class UploadViewModelTest {
 *       @BindValue val fakeDetector: FaceDetector = FakeFaceDetector(faceCount = 1)
 *   }
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DetectionModule {

    @Binds
    @Singleton
    abstract fun bindFaceDetector(impl: MlKitFaceDetector): FaceDetector
}
