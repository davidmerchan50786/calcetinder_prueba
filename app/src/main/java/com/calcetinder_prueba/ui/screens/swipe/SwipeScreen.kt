package com.calcetinder_prueba.ui.screens.swipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.calcetinder_prueba.ui.components.CalcetinderLogo
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.calcetinder_prueba.R
import com.calcetinder_prueba.ui.components.SockCard
import com.calcetinder_prueba.ui.theme.CalcetinderPink
import com.calcetinder_prueba.ui.theme.LikeGreen
import com.calcetinder_prueba.ui.theme.NopeRed
import com.calcetinder_prueba.util.SatiricCopy

@Composable
fun SwipeScreen(
    onNavigateToUpload: () -> Unit,
    viewModel: SwipeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalcetinderLogo(sizeDp = 28.dp)
                    Text(
                        text = SatiricCopy.APP_NAME,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = CalcetinderPink,
                        letterSpacing = 2.sp
                    )
                }
                IconButton(onClick = onNavigateToUpload) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Subir calcetín",
                        tint = CalcetinderPink
                    )
                }
            }

            // Área de cards o estados vacío/loading
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(color = CalcetinderPink)
                    }
                    state.socks.isEmpty() -> {
                        EmptySwipeState(onNavigateToUpload)
                    }
                    else -> {
                        // Renderizamos las 2 primeras cartas (efecto stack)
                        state.socks.asReversed().take(2).forEachIndexed { index, sock ->
                            val isTop = index == state.socks.take(2).size - 1
                            SockCard(
                                sock = sock,
                                onLike = { if (isTop) viewModel.swipeLike(sock) },
                                onNope = { if (isTop) viewModel.swipeNope(sock) },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (!isTop) 8.dp else 0.dp)
                            )
                        }
                    }
                }
            }

            // Botones de acción
            if (!state.isLoading && state.socks.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        onClick = { state.socks.firstOrNull()?.let { viewModel.swipeNope(it) } },
                        icon = Icons.Default.Close,
                        tint = NopeRed,
                        size = 60.dp
                    )
                    ActionButton(
                        onClick = { state.socks.firstOrNull()?.let { viewModel.swipeLike(it) } },
                        icon = Icons.Default.Favorite,
                        tint = LikeGreen,
                        size = 70.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Overlay de MATCH con confetti Lottie
        AnimatedVisibility(
            visible = state.matchMessage != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            MatchOverlay(
                message = state.matchMessage ?: "",
                onDismiss = { viewModel.dismissMatchMessage() }
            )
        }
    }
}

/**
 * Overlay de match con animación Lottie de confetti.
 *
 * Estructura de z-order (de abajo a arriba):
 *  1. Fondo oscuro semitransparente (clickable para dismiss)
 *  2. LottieAnimation confetti — fillMaxSize, encima del fondo
 *  3. Tarjeta de mensaje — centrada, encima del confetti
 *
 * La animación se reproduce 2 veces (iterations = 2) para dar tiempo
 * suficiente a leer el mensaje sin que el confetti desaparezca demasiado pronto.
 */
@Composable
private fun MatchOverlay(message: String, onDismiss: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.confetti)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = 2,
        restartOnPlay = false
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Capa 1: confetti ocupa toda la pantalla
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        // Capa 2: tarjeta con el mensaje, encima del confetti
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(40.dp)
                .background(Color(0xFF1A0A12), RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Text(
                text = message,
                color = LikeGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = SatiricCopy.DISMISS_HINT,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EmptySwipeState(onNavigateToUpload: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = SatiricCopy.SWIPE_EMPTY_TITLE,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = CalcetinderPink,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = SatiricCopy.SWIPE_EMPTY_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = SatiricCopy.SWIPE_UPLOAD_CTA,
            color = CalcetinderPink,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onNavigateToUpload() }
        )
    }
}

/**
 * Botón circular de acción estilo Tinder: fondo blanco, icono de color, sombra elevada.
 * La sombra hace que los botones "floten" sobre el fondo — característica visual clave de Tinder.
 */
@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .shadow(elevation = 8.dp, shape = CircleShape)
            .size(size)
            .background(Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size * 0.48f)
        )
    }
}
