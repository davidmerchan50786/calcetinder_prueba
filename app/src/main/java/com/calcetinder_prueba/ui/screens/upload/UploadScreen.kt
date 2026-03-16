package com.calcetinder_prueba.ui.screens.upload

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.calcetinder_prueba.ui.theme.CalcetinderPink
import com.calcetinder_prueba.ui.theme.NopeRed
import com.calcetinder_prueba.util.SatiricCopy

/**
 * Pantalla de subida de calcetines.
 *
 * Flujo:
 *  1. El usuario elige una foto desde la galería (se pide permiso READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE).
 *  2. [UploadViewModel.onImageSelected] guarda el URI y dispara el análisis ML Kit.
 *  3. Si ML Kit detecta caras, se muestra [UploadUiState.FaceDetected] con un roast satírico
 *     sobre el narcisismo del usuario (overlay rojo a pantalla completa).
 *  4. Si no hay caras, el usuario rellena nombre + descripción y pulsa "Subir".
 *  5. Al completarse con éxito ([UploadUiState.Success]) se navega de vuelta al feed de swipe.
 *
 * @param onUploadSuccess Callback de navegación ejecutado cuando la subida concluye con éxito.
 * @param viewModel ViewModel Hilt que gestiona la lógica de subida y el estado de la UI.
 */
@Composable
fun UploadScreen(
    onUploadSuccess: () -> Unit,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedUri by viewModel.selectedUri.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UploadUiState.Success) {
            viewModel.resetState()
            onUploadSuccess()
        }
    }

    var sockName by remember { mutableStateOf("") }
    var sockDesc by remember { mutableStateOf("") }

    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onImageSelected(it) }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) imageLauncher.launch("image/*")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFAFD))
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = SatiricCopy.UPLOAD_TITLE,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = CalcetinderPink,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = SatiricCopy.UPLOAD_SUBTITLE,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Banner de advertencia anti-ego
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF0F5), RoundedCornerShape(10.dp))
                    .border(1.dp, CalcetinderPink.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = SatiricCopy.UPLOAD_WARNING,
                    fontSize = 12.sp,
                    color = Color(0xFF6B2D4E),
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de imagen
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0E8F0))
                    .border(2.dp, CalcetinderPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { permissionLauncher.launch(imagePermission) },
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Calcetín seleccionado",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CalcetinderPink,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = SatiricCopy.UPLOAD_PICK_IMAGE,
                            color = CalcetinderPink,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre del calcetín
            OutlinedTextField(
                value = sockName,
                onValueChange = { sockName = it },
                label = { Text("Nombre", color = Color.Gray) },
                placeholder = { Text(SatiricCopy.UPLOAD_NAME_HINT, color = Color.LightGray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = uploadFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            OutlinedTextField(
                value = sockDesc,
                onValueChange = { sockDesc = it },
                label = { Text("Descripción", color = Color.Gray) },
                placeholder = { Text(SatiricCopy.UPLOAD_DESC_HINT, color = Color.LightGray, fontSize = 12.sp) },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = uploadFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón subir
            Button(
                onClick = { viewModel.submitSock(sockName, sockDesc) },
                enabled = uiState !is UploadUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = CalcetinderPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (uiState is UploadUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(SatiricCopy.UPLOAD_SUBMIT_BTN, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            // Error genérico
            if (uiState is UploadUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (uiState as UploadUiState.Error).message,
                    color = NopeRed,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Overlay de CASTIGO por cara detectada
        AnimatedVisibility(
            visible = uiState is UploadUiState.FaceDetected,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NopeRed.copy(alpha = 0.96f))
                    .clickable { viewModel.resetState() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(40.dp)
                ) {
                    Text(
                        text = (uiState as? UploadUiState.FaceDetected)?.roastMessage ?: "",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = SatiricCopy.FACE_DISMISS_HINT,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Colores estandarizados para los [OutlinedTextField] del formulario de subida.
 *
 * - Borde activo: [CalcetinderPink] (coherente con el color de marca).
 * - Borde inactivo: rosa muy suave para mantener visibilidad sin distraer.
 * - Texto: oscuro sobre fondo claro, legible en modo diurno.
 */
@Composable
private fun uploadFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CalcetinderPink,
    unfocusedBorderColor = Color(0xFFCCB8C4),
    focusedTextColor = Color(0xFF1A0A12),
    unfocusedTextColor = Color(0xFF1A0A12),
    cursorColor = CalcetinderPink
)
