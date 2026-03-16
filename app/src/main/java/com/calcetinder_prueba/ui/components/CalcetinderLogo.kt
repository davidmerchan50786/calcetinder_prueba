package com.calcetinder_prueba.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Logo satírico de Calcetinder: un calcetín con forma de llama estilo Tinder.
 *
 * El calcetín tiene dos puntas de llama en la apertura de la caña (arriba)
 * y el pie apuntando a la derecha abajo, imitando la silueta de la llama
 * de Tinder pero reconocible como calcetín.
 *
 * @param sizeDp   Ancho del logo en dp; la altura se calcula por ratio (×1.15).
 * @param modifier Modifier estándar de Compose.
 */
@Composable
fun CalcetinderLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 72.dp
) {
    Canvas(modifier = modifier.size(sizeDp, sizeDp * 1.15f)) {
        val gradient = Brush.linearGradient(
            colors = listOf(Color(0xFFFF4458), Color(0xFFFF6B35)),
            start = Offset(size.width * 0.5f, 0f),
            end = Offset(size.width * 0.5f, size.height)
        )
        drawSockFlame(gradient)
    }
}

/**
 * Dibuja el path del calcetín-llama escalado al tamaño del Canvas.
 *
 * Viewport lógico de diseño: 110 × 126 unidades.
 * Anatomía del path (sentido horario desde el lado izquierdo de la caña):
 *   1. Punta izquierda de llama  (y ≈ 0)
 *   2. Valle entre puntas
 *   3. Punta derecha de llama   (y ≈ 0)
 *   4. Lado derecho de la caña bajando
 *   5. Curva del talón → empeine del pie
 *   6. Punta del pie redondeada
 *   7. Suela → talón inferior → sube por la izquierda
 */
private fun DrawScope.drawSockFlame(brush: Brush) {
    val sx = size.width  / 110f
    val sy = size.height / 126f
    fun px(v: Float) = v * sx
    fun py(v: Float) = v * sy

    val path = Path().apply {
        // ── Inicio: lado izquierdo de la caña, bajo las puntas de llama ──
        moveTo(px(20f), py(32f))

        // ── PUNTA IZQUIERDA ──
        cubicTo(px(20f), py(20f), px(22f), py(9f),  px(30f), py(4f))
        cubicTo(px(33f), py(1f),  px(35f), py(0f),  px(38f), py(0f))

        // Valle entre las dos puntas
        cubicTo(px(42f), py(0f),  px(44f), py(11f), px(47f), py(14f))

        // ── PUNTA DERECHA ──
        cubicTo(px(50f), py(9f),  px(54f), py(0f),  px(58f), py(0f))
        cubicTo(px(62f), py(0f),  px(67f), py(7f),  px(70f), py(14f))

        // Bajada por el lado derecho de la apertura
        cubicTo(px(74f), py(20f), px(75f), py(26f), px(75f), py(32f))

        // ── LADO DERECHO DE LA CAÑA bajando ──
        lineTo(px(75f), py(82f))

        // ── TALÓN: curva que conecta caña con empeine ──
        cubicTo(px(75f), py(92f), px(83f), py(96f), px(90f), py(96f))

        // Empeine del pie hacia la punta
        lineTo(px(102f), py(96f))

        // ── PUNTA DEL PIE redondeada ──
        cubicTo(px(112f), py(96f), px(112f), py(114f), px(102f), py(114f))

        // ── SUELA hacia la izquierda ──
        lineTo(px(22f), py(114f))

        // ── TALÓN INFERIOR ──
        cubicTo(px(9f), py(114f), px(5f), py(106f), px(5f), py(100f))

        // ── CURVA IZQUIERDA del talón, subiendo ──
        cubicTo(px(5f), py(90f), px(12f), py(84f), px(20f), py(84f))

        // ── LADO IZQUIERDO DE LA CAÑA subiendo al inicio ──
        lineTo(px(20f), py(32f))

        close()
    }

    drawPath(path, brush = brush)

    // ── Detalle satírico: costura en la punta del pie (3 líneas) ──
    val stitchBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.20f))
    )
    repeat(3) { i ->
        val yOff = py(100f + i * 5f)
        val x1 = px(96f)
        val x2 = px(105f)
        drawLine(
            brush = stitchBrush,
            start = Offset(x1, yOff),
            end = Offset(x2, yOff),
            strokeWidth = px(1.8f)
        )
    }
}
