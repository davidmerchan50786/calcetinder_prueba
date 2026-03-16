package com.calcetinder_prueba.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Abstracción del detector de caras.
 * Permite sustituir ML Kit por un fake en tests sin tocar el ViewModel.
 */
interface FaceDetector {
    /**
     * Analiza la imagen en [uri] y devuelve el número de caras detectadas.
     * Lanza excepción si la imagen no se puede procesar.
     */
    suspend fun detect(uri: Uri): Int
}

/**
 * Implementación de producción usando ML Kit Face Detection.
 * PERFORMANCE_MODE_FAST — suficiente para castigar egos, no necesitamos precisión quirúrgica.
 */
@Singleton
class MlKitFaceDetector @Inject constructor(
    @ApplicationContext private val context: Context
) : FaceDetector {

    override suspend fun detect(uri: Uri): Int = suspendCoroutine { continuation ->
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setMinFaceSize(0.1f)
            .build()
        val detector = FaceDetection.getClient(options)

        try {
            val image = InputImage.fromFilePath(context, uri)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    detector.close()
                    continuation.resume(faces.size)
                }
                .addOnFailureListener { e ->
                    detector.close()
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            detector.close()
            continuation.resumeWithException(e)
        }
    }
}
