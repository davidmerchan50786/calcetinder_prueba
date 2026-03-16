package com.calcetinder_prueba

import android.net.Uri
import com.calcetinder_prueba.data.model.Sock
import com.calcetinder_prueba.data.repository.AuthRepository
import com.calcetinder_prueba.data.repository.SockRepository
import com.calcetinder_prueba.ui.screens.upload.UploadUiState
import com.calcetinder_prueba.ui.screens.upload.UploadViewModel
import com.calcetinder_prueba.util.FaceDetector
import com.calcetinder_prueba.util.MainDispatcherRule
import com.calcetinder_prueba.util.SatiricCopy
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests TDD del castigo de caras en UploadViewModel.
 * Se ejecutan en la JVM — FaceDetector es un fake, SockRepository mockeado.
 *
 * Filosofía TDD aplicada:
 *  RED:   escribe el test que describe el comportamiento deseado
 *  GREEN: el código ya existe (UploadViewModel usa FaceDetector inyectado)
 *  REFACTOR: cada test valida un aspecto específico del castigo
 *
 * Cobertura:
 *  - Validación previa (sin URI, sin nombre, sin sesión)
 *  - Detección de 1 cara → FaceDetected con mensaje del catálogo FACE_ROASTS
 *  - Detección de N > 1 caras → FaceDetected con mensaje especial que incluye el número
 *  - Error en detección → se asume 0 caras, sube igualmente (graceful degradation)
 *  - 0 caras detectadas → procede a upload
 *  - Upload exitoso → Success
 *  - Upload fallido → Error con mensaje
 *  - resetState → limpia URI y estado
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UploadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Fake de FaceDetector configurable por test
    private lateinit var fakeFaceDetector: FaceDetector
    private lateinit var sockRepository: SockRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: UploadViewModel

    private val fakeUri: Uri = mockk(relaxed = true)
    private val fakeSock = Sock(
        id = "sock_1",
        ownerId = "user_1",
        name = "Calcetín sin cara",
        description = "Puro tejido",
        imageUrl = "https://example.com/sock.jpg"
    )

    @Before
    fun setUp() {
        fakeFaceDetector = mockk()
        sockRepository = mockk()
        authRepository = mockk {
            every { currentUserId } returns "user_1"
        }
        viewModel = UploadViewModel(fakeFaceDetector, sockRepository, authRepository)
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun initialState_isIdle_noUri() {
        assertTrue(viewModel.uiState.value is UploadUiState.Idle)
        assertNull(viewModel.selectedUri.value)
    }

    // ── Validaciones previas al submit ────────────────────────────────────────

    @Test
    fun submitSock_withoutUri_setsError() = runTest {
        viewModel.submitSock("Calcetín Fantasma", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.Error
        assertTrue(state.message.contains("imagen", ignoreCase = true))
    }

    @Test
    fun submitSock_withoutName_setsError() = runTest {
        viewModel.onImageSelected(fakeUri)
        viewModel.submitSock("   ", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.Error
        assertTrue(state.message.contains("nombre", ignoreCase = true))
    }

    @Test
    fun submitSock_withoutSession_setsError() = runTest {
        every { authRepository.currentUserId } returns null
        viewModel.onImageSelected(fakeUri)
        viewModel.submitSock("Calcetín", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.Error
        assertTrue(state.message.contains("sesión", ignoreCase = true))
    }

    // ── TDD CASTIGO DE CARAS ──────────────────────────────────────────────────

    @Test
    fun submitSock_oneFaceDetected_setsFaceDetectedState() = runTest {
        // RED → GREEN: si hay 1 cara, el estado debe ser FaceDetected
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 1
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        assertTrue(
            "Con 1 cara detectada, el estado debe ser FaceDetected",
            viewModel.uiState.value is UploadUiState.FaceDetected
        )
    }

    @Test
    fun submitSock_oneFace_roastMessageIsFromCatalog() = runTest {
        // El mensaje para 1 cara debe ser uno de los FACE_ROASTS definidos en SatiricCopy
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 1
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.FaceDetected
        assertTrue(
            "El roast para 1 cara debe pertenecer al catálogo FACE_ROASTS",
            SatiricCopy.FACE_ROASTS.contains(state.roastMessage)
        )
    }

    @Test
    fun submitSock_multipleFacesDetected_setsFaceDetectedWithCount() = runTest {
        // RED → GREEN: con 3 caras, el mensaje debe mencionar el número
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 3
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.FaceDetected
        assertTrue(
            "Con 3 caras, el roast debe mencionar '3'",
            state.roastMessage.contains("3")
        )
    }

    @Test
    fun submitSock_twoFaces_alsoTriggersMultiFaceMessage() = runTest {
        // El umbral es > 1: 2 caras ya activa el mensaje especial
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 2
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.FaceDetected
        assertTrue(state.roastMessage.contains("2"))
    }

    @Test
    fun submitSock_multipleFaces_roastIsNotFromSingleFaceCatalog() = runTest {
        // El mensaje de N caras NO debe estar en la lista de roasts individuales
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 4
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        val state = viewModel.uiState.value as UploadUiState.FaceDetected
        assertTrue(
            "El roast de múltiples caras no debe solaparse con FACE_ROASTS",
            !SatiricCopy.FACE_ROASTS.contains(state.roastMessage)
        )
    }

    // ── Graceful degradation ──────────────────────────────────────────────────

    @Test
    fun submitSock_detectionThrows_assumesZeroFacesAndUploads() = runTest {
        // Si ML Kit explota, no castigamos al usuario inocente
        coEvery { fakeFaceDetector.detect(fakeUri) } throws RuntimeException("ML Kit failure")
        coEvery {
            sockRepository.uploadSock("user_1", "Calcetín", "Descripción", fakeUri)
        } returns Result.success(fakeSock)
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        assertEquals(
            "Si la detección falla, se asume 0 caras y se sube",
            UploadUiState.Success,
            viewModel.uiState.value
        )
    }

    // ── Upload paths ──────────────────────────────────────────────────────────

    @Test
    fun submitSock_noFace_uploadSuccess_setsSuccess() = runTest {
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 0
        coEvery {
            sockRepository.uploadSock("user_1", "Calcetín Puro", "Sin rostro", fakeUri)
        } returns Result.success(fakeSock)
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín Puro", "Sin rostro")

        assertEquals(UploadUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun submitSock_noFace_uploadFailure_setsError() = runTest {
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 0
        coEvery {
            sockRepository.uploadSock(any(), any(), any(), any())
        } returns Result.failure(RuntimeException("Storage error"))
        viewModel.onImageSelected(fakeUri)

        viewModel.submitSock("Calcetín", "Descripción")

        assertTrue(viewModel.uiState.value is UploadUiState.Error)
    }

    // ── resetState ────────────────────────────────────────────────────────────

    @Test
    fun resetState_clearsUriAndReturnsToIdle() = runTest {
        viewModel.onImageSelected(fakeUri)
        viewModel.resetState()

        assertEquals(UploadUiState.Idle, viewModel.uiState.value)
        assertNull(viewModel.selectedUri.value)
    }

    @Test
    fun resetState_afterFaceDetected_allowsRetry() = runTest {
        coEvery { fakeFaceDetector.detect(fakeUri) } returns 1
        viewModel.onImageSelected(fakeUri)
        viewModel.submitSock("Calcetín", "Descripción")
        assertTrue(viewModel.uiState.value is UploadUiState.FaceDetected)

        viewModel.resetState()

        assertEquals(UploadUiState.Idle, viewModel.uiState.value)
        assertNull(
            "Después de reset el usuario debe poder elegir otra imagen",
            viewModel.selectedUri.value
        )
    }
}
