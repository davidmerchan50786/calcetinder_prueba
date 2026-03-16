plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    // Hilt requiere su propio plugin para generar el código de inyección
    alias(libs.plugins.hilt)
    // KSP: procesador de anotaciones para Kotlin 2.x (reemplaza KAPT)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.calcetinder_prueba"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.calcetinder_prueba"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.calcetinder_prueba.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Jetpack Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)

    // Firebase BOM
    val firebaseBom = platform(libs.firebase.bom)
    implementation(firebaseBom)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Coil
    implementation(libs.coil.compose)

    // Lottie — animación confetti en el overlay de match
    implementation(libs.lottie.compose)

    // ML Kit — para detectar caras y castigar narcisistas
    implementation(libs.mlkit.face.detection)

    // Hilt — DI en tiempo de compilación
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // hiltViewModel() para usar ViewModels inyectados directamente en Composables
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Hilt testing — @HiltAndroidTest, @UninstallModules, fakes inyectables
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    // MockK — mocking para Kotlin en tests instrumentados
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}