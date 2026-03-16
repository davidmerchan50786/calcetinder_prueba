package com.calcetinder_prueba.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.calcetinder_prueba.data.model.Sock
import com.calcetinder_prueba.ui.theme.LikeGreen
import com.calcetinder_prueba.ui.theme.NopeRed
import com.calcetinder_prueba.util.SatiricCopy
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val SWIPE_THRESHOLD = 140f

@Composable
fun SockCard(
    sock: Sock,
    onLike: () -> Unit,
    onNope: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val rotation = offsetX.value / 28f
    val overlayAlpha = (abs(offsetX.value) / 250f).coerceIn(0f, 1f)
    val isLiking = offsetX.value > 0

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > SWIPE_THRESHOLD -> {
                                    offsetX.animateTo(2000f, spring(stiffness = Spring.StiffnessLow))
                                    onLike()
                                }
                                offsetX.value < -SWIPE_THRESHOLD -> {
                                    offsetX.animateTo(-2000f, spring(stiffness = Spring.StiffnessLow))
                                    onNope()
                                }
                                else -> {
                                    launch { offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                                    launch { offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                                }
                            }
                        }
                    }
                ) { change, drag ->
                    change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + drag.x)
                        offsetY.snapTo(offsetY.value + drag.y)
                    }
                }
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Imagen del calcetín
            AsyncImage(
                model = sock.imageUrl,
                contentDescription = sock.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente inferior para legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
            )

            // Info del calcetín
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = sock.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sock.description.ifBlank { "Un calcetín de pocas palabras." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2
                )
            }

            // Overlay MATCH (swipe derecha)
            if (overlayAlpha > 0f && isLiking) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LikeGreen.copy(alpha = overlayAlpha * 0.25f))
                ) {
                    Text(
                        text = SatiricCopy.SWIPE_LIKE_LABEL,
                        style = MaterialTheme.typography.headlineLarge,
                        color = LikeGreen,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(28.dp)
                            .rotate(-18f)
                            .border(3.dp, LikeGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Overlay PASO (swipe izquierda)
            if (overlayAlpha > 0f && !isLiking) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NopeRed.copy(alpha = overlayAlpha * 0.25f))
                ) {
                    Text(
                        text = SatiricCopy.SWIPE_NOPE_LABEL,
                        style = MaterialTheme.typography.headlineLarge,
                        color = NopeRed,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(28.dp)
                            .rotate(18f)
                            .border(3.dp, NopeRed, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
