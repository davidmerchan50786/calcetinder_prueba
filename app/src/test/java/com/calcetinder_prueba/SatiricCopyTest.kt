package com.calcetinder_prueba

import com.calcetinder_prueba.util.SatiricCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests para SatiricCopy.
 * Se ejecutan en la JVM local (sin emulador).
 *
 * Cubre:
 *  - Que el copy no esté vacío (regresión contra borrados accidentales)
 *  - La lógica de faceRoast(n): mensaje especial para n > 1, aleatorio para n == 1
 *  - La lógica de matchCompatibility(): devuelve siempre un string del catálogo
 */
class SatiricCopyTest {

    // ── Sanity checks de constantes ───────────────────────────────────────────

    @Test
    fun authTagline_isNotEmpty() {
        assertTrue(SatiricCopy.AUTH_TAGLINE.isNotBlank())
    }

    @Test
    fun uploadWarning_containsKeyword() {
        assertTrue(
            "La advertencia debe mencionar calcetines",
            SatiricCopy.UPLOAD_WARNING.contains("CALCETINES", ignoreCase = true)
        )
    }

    @Test
    fun faceRoastsList_isNotEmpty() {
        assertTrue(SatiricCopy.FACE_ROASTS.isNotEmpty())
    }

    // ── faceRoast(n) ──────────────────────────────────────────────────────────

    @Test
    fun faceRoast_singleFace_returnsOneOfTheRoasts() {
        // faceRoast(1) debe devolver uno de FACE_ROASTS
        val result = SatiricCopy.faceRoast(1)
        assertTrue(
            "faceRoast(1) debe estar dentro de la lista FACE_ROASTS",
            SatiricCopy.FACE_ROASTS.contains(result)
        )
    }

    @Test
    fun faceRoast_multipleFaces_returnsSpecialMessage() {
        val result = SatiricCopy.faceRoast(3)
        assertTrue(
            "Mensaje multi-cara debe mencionar el número de caras",
            result.contains("3")
        )
        // No debe estar en la lista normal de roasts
        assertTrue(
            "Mensaje multi-cara no debe solaparse con la lista individual",
            !SatiricCopy.FACE_ROASTS.contains(result)
        )
    }

    @Test
    fun faceRoast_twoFaces_triggersMultiFaceMessage() {
        // El umbral es > 1, así que 2 ya activa el mensaje especial
        val result = SatiricCopy.faceRoast(2)
        assertTrue(result.contains("2"))
    }

    // ── matchCompatibility() ──────────────────────────────────────────────────

    @Test
    fun matchCompatibility_returnsNonEmptyString() {
        // Comprueba que la función devuelve un valor de la lista pública
        val result = SatiricCopy.matchCompatibility()
        assertNotNull(result)
        assertTrue(result.isNotBlank())
        assertTrue(
            "matchCompatibility() debe devolver una frase del catálogo",
            SatiricCopy.COMPATIBILITY_PHRASES.contains(result)
        )
    }

    @Test
    fun matchCompatibility_allPhrasesAreNonEmpty() {
        // Todas las frases del catálogo deben ser no vacías — test determinista
        SatiricCopy.COMPATIBILITY_PHRASES.forEach { phrase ->
            assertTrue("Frase vacía detectada: '$phrase'", phrase.isNotBlank())
        }
    }

    @Test
    fun matchCompatibility_returnsKnownPhrase() {
        // Itera sobre todo el catálogo de forma determinista
        val knownKeywords = listOf("lana", "textil", "agujeros", "elásticos", "desgaste")
        SatiricCopy.COMPATIBILITY_PHRASES.forEach { phrase ->
            assertTrue(
                "La frase '$phrase' no contiene ninguna palabra clave esperada",
                knownKeywords.any { keyword -> phrase.contains(keyword, ignoreCase = true) }
            )
        }
    }

    // ── matchFoundBody ────────────────────────────────────────────────────────

    @Test
    fun matchFoundBody_isNotEmpty() {
        assertEquals("MATCH DE CALCETINES", SatiricCopy.MATCH_FOUND_TITLE)
        assertTrue(SatiricCopy.MATCH_FOUND_BODY.isNotBlank())
    }
}
