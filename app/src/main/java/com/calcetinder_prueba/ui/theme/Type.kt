package com.calcetinder_prueba.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.calcetinder_prueba.R

// ─────────────────────────────────────────────────────────────────────────────
// Proveedor de Google Fonts
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Proveedor de Google Fonts a través de GMS (Google Mobile Services).
 *
 * Mecanismo: la primera vez que la app arranca con conexión, el proveedor
 * descarga la fuente en el directorio de caché del sistema — sin añadir
 * peso al APK. Los lanzamientos posteriores la usan desde caché.
 *
 * Certificados: definidos en `res/values/font_certs.xml` (AOSP sample).
 * Sin GMS (emulador sin Play Services), cae silenciosamente a Roboto.
 */
private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// ─────────────────────────────────────────────────────────────────────────────
// Familia tipográfica: Poppins
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Poppins — sans-serif geométrica de código abierto (Google Fonts).
 *
 * Elegida por:
 *  - Proporciones similares a la fuente que usa Tinder internamente.
 *  - Excelente legibilidad a tamaños pequeños y medianos.
 *  - Cinco grosores registrados para soportar todos los estilos del tema.
 */
private val poppinsFont = GoogleFont("Poppins")

val PoppinsFamily = FontFamily(
    Font(googleFont = poppinsFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = poppinsFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = poppinsFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = poppinsFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    Font(googleFont = poppinsFont, fontProvider = googleFontProvider, weight = FontWeight.Black),
)

// ─────────────────────────────────────────────────────────────────────────────
// Sistema tipográfico de Material3
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Escala tipográfica del tema Calcetinder.
 *
 * Todos los estilos usan [PoppinsFamily]. Los tamaños y pesos siguen
 * la escala Material3 estándar adaptada al diseño Tinder:
 *  - Headlines: contenido destacado y títulos de pantalla.
 *  - Body: párrafos y descripciones de calcetines.
 *  - Label: botones, badges y texto de apoyo.
 */
val CalcetinderTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
    ),
)
