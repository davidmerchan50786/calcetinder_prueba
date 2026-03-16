# ─── Calcetinder ProGuard Rules ───────────────────────────────────────────────
# Preservar números de línea en stack traces de producción
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ─── Firebase ─────────────────────────────────────────────────────────────────
# Firebase usa reflexión para serializar/deserializar documentos Firestore
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepnames class com.google.firebase.** { *; }
# Modelos de datos: Firestore necesita el constructor vacío y todos los campos
-keepclassmembers class com.calcetinder_prueba.data.model.** {
    <init>();
    <fields>;
}

# ─── Hilt / Dagger ────────────────────────────────────────────────────────────
# Hilt genera código en tiempo de compilación; los nombres de las clases generadas
# deben preservarse para que la inyección funcione en release
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# ─── ML Kit Face Detection ────────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.vision.** { *; }

# ─── Lottie ───────────────────────────────────────────────────────────────────
-keep class com.airbnb.lottie.** { *; }

# ─── Coil ─────────────────────────────────────────────────────────────────────
-keep class coil.** { *; }

# ─── Kotlin ───────────────────────────────────────────────────────────────────
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
# Sealed classes necesitan sus subclases para el pattern matching
-keepclassmembers class * extends kotlin.reflect.KClass { *; }

# ─── Compose ──────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
