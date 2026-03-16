package com.calcetinder_prueba.util

/**
 * Fuente única de verdad para el copy satírico de Calcetinder.
 * Centralizado aquí para facilitar localización futura y para
 * que el equipo pueda reír (o llorar) en un solo lugar.
 */
object SatiricCopy {

    // ── AUTH ──────────────────────────────────────────────────────────────────

    const val AUTH_TAGLINE =
        "La única app donde lo que llevas en los pies\nimporta más que lo que llevas en la cabeza."

    const val AUTH_EMAIL_HINT = "tu@correo.com"
    const val AUTH_PASSWORD_HINT = "Algo que recuerdas, a diferencia de tus ex"
    const val AUTH_LOGIN_BTN = "Entrar"
    const val AUTH_REGISTER_BTN = "Unirme a la causa perdida"
    const val AUTH_TOGGLE_TO_REGISTER = "¿Primera vez aquí? Crea una cuenta."
    const val AUTH_TOGGLE_TO_LOGIN = "¿Ya tienes cuenta? Entra si puedes."
    const val AUTH_FOOTER =
        "Ningún algoritmo de compatibilidad. Ningún coach de citas.\nSolo calcetines y honestidad brutal."

    const val AUTH_ERROR_WRONG_PASSWORD = "Contraseña incorrecta.\nComo la mayoría de tus decisiones románticas."
    const val AUTH_ERROR_NO_USER = "Usuario no encontrado.\nIgual que tus expectativas."
    const val AUTH_ERROR_WEAK_PASSWORD = "Contraseña demasiado débil.\nComo tu argumento para salir en foto de perfil."
    const val AUTH_ERROR_ALREADY_EXISTS = "Este email ya existe.\nAlguien se adelantó a tu crisis existencial."
    const val AUTH_ERROR_GENERIC = "Algo salió mal.\nBienvenido a la experiencia humana completa."

    // ── UPLOAD ────────────────────────────────────────────────────────────────

    const val UPLOAD_TITLE = "EL CALCETÍN,\nPROTAGONISTA"
    const val UPLOAD_SUBTITLE = "Tú eres el soporte. El calcetín, la obra."

    const val UPLOAD_WARNING =
        "ZONA LIBRE DE EGO\n\n" +
        "Aquí suben CALCETINES.\n" +
        "No caras. No cuerpos. No 'mejores ángulos'.\n" +
        "No tu mascota mirando a cámara.\n\n" +
        "Si tienes un calcetín interesante, eres bienvenido.\n" +
        "Si solo tienes una cara fotogénica, Instagram te espera.\n" +
        "Ellos te merecen."

    const val UPLOAD_PICK_IMAGE = "Elegir calcetín"
    const val UPLOAD_NAME_HINT = "Nombre del calcetín (no el tuyo)"
    const val UPLOAD_DESC_HINT =
        "Cuéntanos sobre este calcetín.\nSus traumas, su historia, sus agujeros.\nNO los tuyos."
    const val UPLOAD_SUBMIT_BTN = "Subir calcetín"
    const val UPLOAD_SUCCESS = "Calcetín subido con éxito.\nEl mundo es objetivamente un lugar mejor ahora."

    // ── DETECCIÓN DE CARAS (el corazón de la app) ─────────────────────────────

    val FACE_ROASTS = listOf(
        "CARA DETECTADA.\n\n" +
        "Esto es Calcetinder, no tu portfolio de LinkedIn.\n" +
        "El sistema rechaza tu oferta de protagonismo.\n" +
        "Intenta con un calcetín.",

        "EGO HUMANO DETECTADO.\n\n" +
        "El algoritmo anti-narcisismo ha activado\nel protocolo de rechazo automático.\n" +
        "Somos inmunes a tus encantos.",

        "ERROR 418: SOY UNA TETERA.\n\n" +
        "Y esta app es para calcetines,\nno para tu colección de selfies.\n" +
        "El tejido o nada.",

        "DETECCIÓN COMPLETADA.\n\n" +
        "Caras detectadas: 1.\n" +
        "Calcetines detectados: 0.\n" +
        "Relación señal/ruido: inaceptable.\n" +
        "Imagen rechazada con afecto.",

        "HUMANIDAD DETECTADA.\n\n" +
        "Los humanos son el packaging,\nno el producto.\n" +
        "Por favor, sube tejidos, no traumas.",

        "TU CARA NO ES UN CALCETÍN.\n\n" +
        "Por mucho que lo intentes,\npor mucho filtro que uses,\npor mucho que lo desees.\n" +
        "Las leyes de la física son inflexibles.\n" +
        "Y las nuestras, también."
    )

    fun faceRoast(faceCount: Int): String {
        return if (faceCount > 1) {
            "COLECCIÓN DE EGOS DETECTADA.\n\n" +
            "$faceCount caras encontradas en una sola imagen.\n" +
            "Nivel de narcisismo: estadísticamente improbable.\n" +
            "Rechazado en bloque.\n" +
            "Con cariño."
        } else {
            FACE_ROASTS.random()
        }
    }

    // ── SWIPE ─────────────────────────────────────────────────────────────────

    const val SWIPE_LIKE_LABEL = "MATCH"
    const val SWIPE_NOPE_LABEL = "PASO"

    const val SWIPE_EMPTY_TITLE = "FIN DEL CATÁLOGO"
    const val SWIPE_EMPTY_BODY =
        "Te has visto todos los calcetines disponibles.\n\n" +
        "Como en la vida real:\nlos buenos desaparecen\ny tú te quedas mirando el vacío."

    const val SWIPE_UPLOAD_CTA = "Sube el tuyo"

    // ── MATCHES ───────────────────────────────────────────────────────────────

    const val MATCHES_TITLE = "TUS MATCHES"

    const val MATCHES_EMPTY_TITLE = "NINGÚN MATCH"
    const val MATCHES_EMPTY_BODY =
        "El calcetín perfecto existe.\n\nSolo que no sabe que existes."

    const val MATCH_FOUND_TITLE = "MATCH DE CALCETINES"
    const val MATCH_FOUND_BODY =
        "Dos prendas menores han encontrado su camino.\nHumanidad: 0. Mercería: 1."

    fun matchCompatibility(): String {
        val percentages = listOf(
            "Compatibilidad: 94% (medida en lana)",
            "Afinidad textil: elevada",
            "Coincidencia de agujeros: notable",
            "Sintonía de elásticos: prometedora",
            "Nivel de desgaste: complementario"
        )
        return percentages.random()
    }

    // ── PERFIL ─────────────────────────────────────────────────────────────────

    const val PROFILE_TITLE = "EL PORTADOR"
    const val PROFILE_SUBTITLE =
        "El calcetín es la estrella.\nTú, el soporte técnico."

    const val PROFILE_ROLE_LABEL = "ROL EN EL ECOSISTEMA"
    const val PROFILE_ROLE_VALUE = "Contenedor de calcetines con ego controlado"

    const val PROFILE_LOGOUT_BTN = "Abandonar la causa"
    const val PROFILE_LOGOUT_CONFIRM =
        "Puedes salir.\nLos calcetines seguirán aquí sin ti.\nComo siempre."
}
