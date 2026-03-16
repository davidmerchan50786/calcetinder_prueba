# CALCETINDER — Documentación Técnica Completa

> La única app de citas donde tu cara no importa.
> Una sátira funcional contra el narcisismo, el marketing vacío y la cosificación digital.

---

## Índice

1. [Descripción del Proyecto](#1-descripción-del-proyecto)
2. [Stack Tecnológico](#2-stack-tecnológico)
3. [Arquitectura del Sistema](#3-arquitectura-del-sistema)
4. [Estructura de Ficheros](#4-estructura-de-ficheros)
5. [Capa de Datos — Firebase](#5-capa-de-datos--firebase)
6. [Modelo de Seguridad](#6-modelo-de-seguridad)
7. [Feature Principal: Detección Anti-Narcisismo](#7-feature-principal-detección-anti-narcisismo)
8. [Navegación y Flujo de Usuario](#8-navegación-y-flujo-de-usuario)
9. [Sistema de Copy Satírico](#9-sistema-de-copy-satírico)
10. [Decisiones Técnicas Justificadas](#10-decisiones-técnicas-justificadas)
11. [Setup del Proyecto](#11-setup-del-proyecto)
12. [Deuda Técnica y Roadmap](#12-deuda-técnica-y-roadmap)
13. [Preguntas Frecuentes Técnicas](#13-preguntas-frecuentes-técnicas)

---

## 1. Descripción del Proyecto

**Calcetinder** es una aplicación Android de citas para calcetines —no para personas.

### Propósito satírico
El proyecto es una crítica directa y funcional a:
- El narcisismo digital y la cultura del selfie
- La cosificación en apps de citas (juzgar por apariencia física)
- El marketing vacío y el lenguaje corporativo de "conexiones auténticas"
- El ego como commodity en redes sociales

### Cómo funciona la sátira técnicamente
1. Los usuarios suben **fotos de calcetines**, no de sí mismos
2. Si intentan subir una foto con una cara humana, **ML Kit detecta el ego** y rechaza la imagen con un mensaje demoledor
3. El match ocurre entre calcetines, nunca entre cuerpos
4. Todo el copy de la aplicación está diseñado para ridiculizar el comportamiento narcisista

---

## 2. Stack Tecnológico

### Android

| Tecnología | Versión | Justificación |
|------------|---------|---------------|
| Kotlin | 2.1.0 | Lenguaje oficial Android. Null-safety, coroutines, expresividad. |
| AGP (Android Gradle Plugin) | 9.1.0 | Versión más reciente estable. |
| Jetpack Compose | BOM 2024.10.01 | UI declarativa moderna. Elimina 60% del boilerplate de XML. |
| Navigation Compose | 2.8.3 | Navegación type-safe integrada con Compose. |
| Lifecycle / ViewModel | 2.8.5 | Supervivencia a rotación de pantalla. Separación UI/lógica. |
| Coil | 2.6.0 | Carga de imágenes nativa Kotlin. Más eficiente que Glide/Picasso para Compose. |
| Coroutines | 1.8.1 | Operaciones asíncronas sin callback hell. |
| Hilt | 2.51.1 | Inyección de dependencias en tiempo de compilación. Elimina instancias duplicadas. |
| Lottie Compose | 6.4.0 | Animaciones JSON en Compose. Confetti en el overlay de match. |
| MockK | 1.13.10 | Mocking para Kotlin en tests (JVM + Android instrumented). |

### Backend (Firebase)

| Servicio | Uso |
|----------|-----|
| Firebase Auth | Registro/login por email + contraseña |
| Cloud Firestore | Base de datos NoSQL para calcetines, swipes, matches |
| Firebase Storage | Almacenamiento de imágenes de calcetines |

### ML Kit

| Librería | Versión | Uso |
|----------|---------|-----|
| `com.google.mlkit:face-detection` | 17.1.0 | Detectar caras en imágenes antes de subirlas |

### Build System

| Herramienta | Detalle |
|-------------|---------|
| Gradle Kotlin DSL | `build.gradle.kts` — mejor IDE support, type-safe |
| Version Catalog | `libs.versions.toml` — fuente única de verdad para versiones |
| Kotlin Compose Plugin | `org.jetbrains.kotlin.plugin.compose` — Kotlin 2.0+ elimina `kotlinCompilerExtensionVersion` manual |
| KSP 2.1.0-1.0.29 | Procesador de anotaciones para Kotlin 2.x (reemplaza KAPT). Requerido por Hilt. |

---

## 3. Arquitectura del Sistema

### Patrón: MVVM + Repository

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │AuthScreen│  │SwipeScreen│  │UploadScr │  │MatchesSc │   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘   │
│       │              │              │              │         │
│  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐  │
│  │AuthVM    │  │SwipeVM   │  │UploadVM  │  │MatchesVM │  │ProfileVM │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
└───────┼──────────────┼──────────────┼──────────────┼──────────────┼──────┘
        │              │              │              │
┌───────▼──────────────▼──────────────▼──────────────▼────────┐
│                    DATA LAYER (Repositories)                  │
│         AuthRepository  SockRepository  MatchRepository      │
└───────────────────────────────────┬──────────────────────────┘
                                    │
┌───────────────────────────────────▼──────────────────────────┐
│                    FIREBASE (BaaS)                            │
│         Auth         Firestore           Storage             │
└───────────────────────────────────────────────────────────────┘
```

### Por qué MVVM

- **Testabilidad**: Los ViewModels no conocen la UI. Se pueden testear con JUnit sin emulador.
- **Separación de responsabilidades**: Las pantallas solo observan estado; toda la lógica vive en el ViewModel.
- **Supervivencia a rotación**: Los ViewModels sobreviven a cambios de configuración; los Composables no.

### Hilt como grafo de dependencias

Todos los ViewModels usan `@HiltViewModel @Inject constructor(...)`. Los repositorios son `@Singleton @Inject constructor()`. Hilt garantiza una única instancia compartida de cada repositorio en toda la app.

```
@HiltAndroidApp CalcetinderApp
    └── @AndroidEntryPoint MainActivity
            └── hiltViewModel() → @HiltViewModel AuthViewModel
                                               UploadViewModel  ← FaceDetector (interfaz)
                                               SwipeViewModel   ← SockRepository @Singleton
                                               MatchesViewModel ← MatchRepository @Singleton
                                               ProfileViewModel ← AuthRepository @Singleton
```

`DetectionModule` vincula `FaceDetector` (interfaz) con `MlKitFaceDetector` (producción). En tests se sustituye via `@UninstallModules(DetectionModule::class) + @BindValue`.

### Por qué Repository Pattern

- **Abstracción de fuente de datos**: El ViewModel no sabe si los datos vienen de Firebase, Room, o una API REST. En el futuro se puede cambiar sin tocar la UI.
- **Testabilidad con mocks**: Se puede hacer un `FakeSockRepository` para tests sin conectarse a Firebase.
- **Responsabilidad única**: Cada repositorio gestiona un dominio específico (Auth, Socks, Matches).

---

## 4. Estructura de Ficheros

```
calcetinder_prueba/
│
├── app/
│   ├── google-services.json          ← Config Firebase (NO subir a repo público)
│   ├── build.gradle.kts              ← Dependencias del módulo app
│   └── src/main/
│       ├── AndroidManifest.xml       ← Permisos: INTERNET, READ_MEDIA_IMAGES
│       ├── java/com/calcetinder_prueba/
│       │   │
│       │   ├── MainActivity.kt       ← Entry point. Solo lanza AppNavigation()
│       │   │
│       │   ├── navigation/
│       │   │   └── AppNavigation.kt  ← NavHost + BottomNav. Mapa de rutas tipadas.
│       │   │
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── User.kt       ← Perfil de usuario (id, email, createdAt)
│       │   │   │   ├── Sock.kt       ← Entidad calcetín (id, ownerId, name, imageUrl...)
│       │   │   │   ├── Match.kt      ← Emparejamiento de dos calcetines
│       │   │   │   └── Swipe.kt      ← Registro de un swipe (like/nope)
│       │   │   │
│       │   │   └── repository/
│       │   │       ├── AuthRepository.kt    ← signIn, signUp, signOut, currentUser
│       │   │       ├── SockRepository.kt    ← getSocksToSwipe, uploadSock, swipeOnSock
│       │   │       └── MatchRepository.kt   ← getMatches (Flow reactivo)
│       │   │
│       │   ├── ui/
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt      ← Paleta: CalcetinderPink, LikeGreen, NopeRed...
│       │   │   │   ├── Theme.kt      ← CalcetinderTheme (Material3 lightColorScheme)
│       │   │   │   └── Type.kt       ← Tipografía: headline, body, label
│       │   │   │
│       │   │   ├── components/
│       │   │   │   └── SockCard.kt   ← Tarjeta arrastrable con animación spring
│       │   │   │
│       │   │   └── screens/
│       │   │       ├── auth/
│       │   │       │   ├── AuthScreen.kt      ← Login/registro. Fondo negro. Copy ácido.
│       │   │       │   └── AuthViewModel.kt   ← Estado: Idle/Loading/Success/Error
│       │   │       ├── upload/
│       │   │       │   ├── UploadScreen.kt    ← Picker + warnings + overlay castigo
│       │   │       │   └── UploadViewModel.kt ← ML Kit face detection + upload Firebase
│       │   │       ├── swipe/
│       │   │       │   ├── SwipeScreen.kt     ← Stack de cartas + overlay de match
│       │   │       │   └── SwipeViewModel.kt  ← Carga socks + gestiona swipes + match
│       │   │       └── matches/
│       │   │           ├── MatchesScreen.kt   ← Lista de matches con thumbnails
│       │   │           └── MatchesViewModel.kt← Flow reactivo de matches del usuario
│       │   │       └── profile/
│       │   │           ├── ProfileScreen.kt   ← Email, ID, rol satírico, logout
│       │   │           └── ProfileViewModel.kt← userEmail, userIdShort, logout()
│       │   │
│       │   ├── di/
│       │   │   └── DetectionModule.kt ← @Binds FaceDetector → MlKitFaceDetector
│       │   │
│       │   └── util/
│       │       ├── SatiricCopy.kt    ← FUENTE ÚNICA de todo el texto satírico
│       │       └── FaceDetector.kt   ← Interfaz + MlKitFaceDetector (inyectable, testeable)
│       │
│       └── res/
│           ├── drawable/             ← 12 drawables XML (botones, fondos, iconos)
│           ├── mipmap-*/             ← Iconos de app en densidades estándar
│           ├── raw/
│           │   └── confetti.json     ← Animación Lottie (reservado para celebración match)
│           ├── values/
│           │   ├── colors.xml        ← Colores de recursos (para launcher icon, etc.)
│           │   ├── strings.xml       ← app_name = "Calcetinder"
│           │   └── themes.xml        ← Theme.Calcetinder_prueba (Material3 NoActionBar)
│           └── values-night/
│               └── themes.xml        ← Compose gestiona dark mode programáticamente
│
├── gradle/
│   ├── libs.versions.toml            ← Version Catalog: FUENTE ÚNICA de versiones
│   └── wrapper/gradle-wrapper.*
│
├── firebase_rules/                   ← ACCIÓN MANUAL REQUERIDA: pegar en consola Firebase
│   ├── firestore.rules               ← Reglas Firestore (auth obligatoria)
│   └── storage.rules                 ← Reglas Storage (solo imágenes, max 5MB)
│
├── build.gradle.kts                  ← Plugins raíz (android, kotlin, compose, gservices)
├── settings.gradle.kts               ← rootProject.name, repositorios
├── gradle.properties
└── ARCHITECTURE.md                   ← Este archivo
```

---

## 5. Capa de Datos — Firebase

### Esquema Firestore

#### Colección `/users/{userId}`
```json
{
  "id": "firebase_uid",
  "email": "usuario@ejemplo.com",
  "displayName": "",
  "createdAt": "Timestamp"
}
```

#### Colección `/socks/{sockId}`
```json
{
  "id": "auto_generated",
  "ownerId": "firebase_uid",
  "name": "El Calcetín Existencial",
  "description": "Ha sobrevivido tres lavadoras. Tiene carácter.",
  "imageUrl": "https://firebasestorage.../socks/userId/uuid.jpg",
  "uploadedAt": "Timestamp"
}
```
**Índice recomendado**: `ownerId ASC, uploadedAt DESC`

#### Colección `/swipes/{swipeId}`
```json
{
  "id": "auto_generated",
  "swiperId": "firebase_uid_del_que_swipea",
  "sockId": "id_del_calcetín_swipeado",
  "sockOwnerId": "firebase_uid_dueño_del_calcetín",
  "direction": "like | nope",
  "timestamp": "Timestamp"
}
```
**Índices recomendados**:
- `swiperId ASC, direction ASC` (para consulta de match inverso)
- `swiperId ASC, sockOwnerId ASC, direction ASC` (para detectar match)

#### Colección `/matches/{matchId}`
```json
{
  "id": "auto_generated",
  "user1Id": "firebase_uid",
  "user2Id": "firebase_uid",
  "sock1Id": "calcetín_de_user1",
  "sock2Id": "calcetín_de_user2",
  "sock1ImageUrl": "https://...",
  "sock2ImageUrl": "https://...",
  "sock1Name": "El Calcetín Existencial",
  "sock2Name": "Calcetín de Rayas Melancólicas",
  "createdAt": "Timestamp"
}
```
**Índice recomendado**: `user1Id ASC, createdAt DESC` y `user2Id ASC, createdAt DESC`

### Lógica de Match

Cuando el usuario A da like al calcetín X (de usuario B):

```
1. Guardar swipe(swiperId=A, sockId=X, sockOwnerId=B, direction="like")
2. Consultar: swipes WHERE swiperId=B AND sockOwnerId=A AND direction="like" LIMIT 1
3. Si existe → crear match(user1Id=A, user2Id=B, sock1Id=swipe.sockId, sock2Id=X)
4. Retornar isMatch=true → UI muestra overlay de celebración
```

**Complejidad**: O(1) por el índice en Firestore. La consulta de match inverso es un punto único de lectura.

### Firebase Storage

Estructura de paths:
```
/socks/{userId}/{uuid}.jpg
```

Límites aplicados en Storage Rules:
- Tamaño máximo: 5 MB por imagen
- Solo tipos `image/*`
- Solo el propietario puede escribir en su carpeta

---

## 6. Modelo de Seguridad

### Reglas Firestore (`firebase_rules/firestore.rules`)

| Colección | Lectura | Escritura |
|-----------|---------|-----------|
| `/users/{userId}` | Auth requerida | Solo el propio usuario |
| `/socks/{sockId}` | Auth requerida | Propietario (ownerId == uid) |
| `/swipes/{swipeId}` | Swiper o dueño del calcetín | Solo el swiper (swiperId == uid) |
| `/matches/{matchId}` | Solo user1 o user2 | Solo user1 o user2 |

**Principio de mínimo privilegio**: el dueño de un calcetín puede leer los swipes sobre sus calcetines (necesario para detectar match mutuo desde el cliente). La creación de matches requiere que el creador sea uno de los participantes.

**Los matches son inmutables** (`allow delete: if false`): como los traumas, permanentes.

### Reglas Storage (`firebase_rules/storage.rules`)

- Solo el propietario puede subir en su carpeta `/socks/{userId}/`
- Restricción de tipo MIME: `image/*`
- Límite de 5 MB
- Cualquier otra ruta: denegada explícitamente

### ACCIÓN REQUERIDA

Los archivos en `firebase_rules/` son de referencia. Para aplicarlos:
1. Ir a [Firebase Console](https://console.firebase.google.com/)
2. Seleccionar el proyecto
3. Firestore → Reglas → Pegar contenido de `firestore.rules` → Publicar
4. Storage → Reglas → Pegar contenido de `storage.rules` → Publicar

### Secretos y API Keys

**`google-services.json` contiene claves de API de Firebase.**

Para repositorios públicos, añadir a `.gitignore`:
```
app/google-services.json
```

Añadir un `google-services.json.example` al repositorio con valores ficticios para que otros desarrolladores sepan qué estructura crear.

---

## 7. Feature Principal: Detección Anti-Narcisismo

### Flujo completo

```
Usuario selecciona imagen de galería
        │
        ▼
UploadViewModel.onImageSelected(uri)
        │
        ▼
Usuario pulsa "Subir calcetín"
        │
        ▼
UploadViewModel.submitSock()
        │
        ▼
[Validación local: nombre no vacío, URI no nulo]
        │
        ▼
detectFaces(uri)  ←── ML Kit Face Detection
        │
   ┌────┴────┐
   │         │
faces > 0   faces = 0
   │         │
   ▼         ▼
FaceDetected  Firebase Storage upload
(roast msg)       │
                  ▼
             Firestore save (Sock)
                  │
                  ▼
             UploadUiState.Success
                  │
                  ▼
             navigate → SwipeScreen
```

### Implementación de detectFaces

```kotlin
// UploadViewModel.kt
private suspend fun detectFaces(uri: Uri): Int = suspendCoroutine { continuation ->
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setMinFaceSize(0.1f)  // Detecta caras que ocupen al menos 10% de la imagen
        .build()

    val detector = FaceDetection.getClient(options)
    val image = InputImage.fromFilePath(getApplication(), uri)

    detector.process(image)
        .addOnSuccessListener { faces -> detector.close(); continuation.resume(faces.size) }
        .addOnFailureListener { e -> detector.close(); continuation.resumeWithException(e) }
}
```

### ¿Por qué suspendCoroutine y no addOnSuccessListener directo?

ML Kit usa callbacks de Google Tasks. `suspendCoroutine` convierte ese callback en una función suspendable, permitiendo usar la detección dentro de una coroutine sin bloquear el hilo y sin anidar callbacks.

### Mensajes de castigo (SatiricCopy.faceRoast)

- Si `faceCount == 1`: mensaje aleatorio de la lista `FACE_ROASTS` (6 variantes)
- Si `faceCount > 1`: mensaje especial que menciona el número exacto de egos detectados
- El overlay ocupa el 96% de opacidad con fondo `NopeRed`
- Se descarta tocando cualquier parte de la pantalla

### Limitaciones conocidas

| Caso | Comportamiento |
|------|---------------|
| Cara muy pequeña (< 10% imagen) | Puede no detectarse → falso negativo |
| Cara muy angulada | ML Kit FAST puede fallar → falso negativo |
| Primer uso sin WiFi | El modelo tarda en descargarse (Play Services) |
| Imagen corrupta | `resumeWithException` → `Result.failure` → no bloquea al usuario |

---

## 8. Navegación y Flujo de Usuario

### Mapa de pantallas

```
                    ┌─────────────┐
                    │  AuthScreen │  ← Punto de entrada
                    │  (fondo negro)│
                    └──────┬──────┘
                           │ onAuthSuccess (login o registro exitoso)
                           │ [popUpTo Auth, inclusive=true]
                           ▼
              ┌────────────────────────┐
              │      SwipeScreen       │ ← Home (BottomNav visible)
              │   (stack de tarjetas)  │
              └──────┬─────────┬───────┘
                     │         │
          onNavigate │         │ BottomNav
          ToUpload   │         ▼
                     │  ┌─────────────┐
                     │  │MatchesScreen│ ← (BottomNav visible)
                     │  └─────────────┘
                     ▼
              ┌─────────────────┐
              │  UploadScreen   │ ← Sin BottomNav (flujo de onboarding/creación)
              │  (detector ego) │
              └────────┬────────┘
                       │ onUploadSuccess
                       │ [popUpTo Upload, inclusive=true]
                       ▼
                 SwipeScreen
```

### Gestión del back stack

- **Auth → Swipe**: `popUpTo(Auth, inclusive=true)` — el usuario no puede volver al login con el botón atrás
- **Upload → Swipe**: `popUpTo(Upload, inclusive=true)` — tras subir un calcetín, no tiene sentido volver al formulario
- **BottomNav**: `launchSingleTop=true, restoreState=true` — comportamiento estándar Material Design

### BottomNav condicional

```kotlin
private val bottomNavScreens = listOf(
    Screen.Swipe.route, Screen.Matches.route, Screen.Profile.route
)

// Solo visible cuando currentRoute está en bottomNavScreens
if (currentRoute in bottomNavScreens) { NavigationBar(...) }
// 3 tabs: Calcetines (Home), Matches (Favorite), Perfil (Person)
```

---

## 9. Sistema de Copy Satírico

### SatiricCopy.kt — Fuente única de verdad

Todas las cadenas de texto satírico están centralizadas en `util/SatiricCopy.kt`.

**Por qué no en strings.xml**:
- Las strings.xml son para localización. El copy satírico tiene una voz muy específica que normalmente no se localiza.
- Centralizar en un objeto Kotlin permite usar lógica (listas, `.random()`, interpolación de variables).
- Si en el futuro se quiere localizar, la migración es trivial: `SatiricCopy.faceRoast(n)` → `context.getString(R.string.face_roast, n)`.

### Categorías de copy

| Categoría | Descripción |
|-----------|-------------|
| `AUTH_*` | Pantalla de login/registro — taglines y mensajes de error humanizados |
| `UPLOAD_*` | Pantalla de subida — advertencias anti-ego, success message |
| `FACE_ROASTS` | Lista de 6 mensajes de rechazo por cara detectada |
| `faceRoast(n)` | Función: devuelve mensaje según número de caras |
| `SWIPE_*` | Estado vacío del swipe, labels de MATCH/PASO |
| `MATCHES_*` | Estado vacío y mensajes de match encontrado |
| `matchCompatibility()` | Función: devuelve una de 5 frases de "compatibilidad" aleatoria |
| `PROFILE_*` | Pantalla de perfil — rol satírico, copy de logout |

---

## 10. Decisiones Técnicas Justificadas

### DT-001: Jetpack Compose vs XML Views
Compose elimina el boilerplate de View Binding, reduce un 40-60% el código de UI, y tiene mejor soporte para animaciones reactivas (como el swipe con `Animatable`). El proyecto fue iniciado con XML pero migrado inmediatamente.

### DT-002: Firebase como BaaS
Para un MVP educativo/satírico, Firebase ofrece Auth + base de datos + storage con cero infraestructura propia. El riesgo de vendor lock-in es aceptable en esta fase. Si el proyecto escala, la capa Repository facilita migrar a un backend propio.

### DT-003: ML Kit Face Detection (Play Services, no bundled)
El modelo se descarga vía Google Play Services, manteniendo el APK ligero. La alternativa (modelo bundled) añade ~16 MB al APK. Para un proyecto educativo, Play Services es suficiente.

### DT-004: suspendCoroutine para ML Kit
ML Kit no tiene soporte nativo de coroutines. `suspendCoroutine` es el puente idiomático en Kotlin para convertir callbacks en código secuencial sin bloquear el hilo.

### DT-005 (REEMPLAZADO por DT-009): UploadViewModel con @ApplicationContext
La versión original usaba `AndroidViewModel(application)`. Ahora `UploadViewModel` recibe `@ApplicationContext Context` inyectado por Hilt — más limpio y testeable. Ver DT-009.

### DT-009: FaceDetector como interfaz inyectable
La detección de caras se extrajo de `UploadViewModel` a una interfaz `FaceDetector` con implementación `MlKitFaceDetector`. Esto permite sustituir ML Kit en tests con un `FakeFaceDetector(faceCount = N)` sin necesidad de emulador ni Google Play Services. `DetectionModule` vincula la interfaz con la implementación de producción via `@Binds`.

### DT-010: Hilt DI — grafo de dependencias compilado
Los repositorios eran instanciados directamente en los ViewModels (`MatchRepository()`), generando instancias duplicadas. Con `@Singleton @Inject constructor()` + `@HiltViewModel`, Hilt garantiza una única instancia por repositorio en todo el ciclo de vida de la app. KSP genera el código en tiempo de compilación (sin reflexión en runtime).

### DT-011: Lottie para animación de match
La animación confetti en el overlay de match usa `lottie-compose` 6.4.0. El archivo `confetti.json` vive en `res/raw/`. `animateLottieCompositionAsState(iterations=2)` reproduce la animación dos veces al aparecer el overlay, dando tiempo suficiente para leer el mensaje sin loopearse infinitamente.

### DT-006: Detección de match sin transacciones Firestore
La detección de match usa dos operaciones separadas (guardar swipe + consultar match inverso) en lugar de una transacción atómica. El riesgo es una condición de carrera si dos usuarios dan like simultáneamente: podrían crearse dos documentos de match. Para el volumen educativo actual, es aceptable. Para producción real: usar Firestore transactions o Cloud Functions.

### DT-007: Filtrado de socks en cliente
`getSocksToSwipe` carga todos los socks no propios y filtra los ya swiped en cliente. Firestore `whereNotIn` tiene límite de 10 elementos. Para un proyecto educativo (< 100 socks), es aceptable. Para producción: paginación + cursor + índice compuesto.

### DT-008 (RESUELTO): Hilt migrado
Ver DT-010.

---

## 11. Setup del Proyecto

### Prerrequisitos

- Android Studio Ladybug (2024.2.x) o superior
- JDK 17
- Cuenta en [Firebase Console](https://console.firebase.google.com/)

### Paso 1: Clonar y abrir

```bash
git clone <repo>
cd calcetinder_prueba
```

Abrir en Android Studio con "Open Existing Project".

### Paso 2: Configurar Firebase

1. Crear un proyecto en Firebase Console
2. Habilitar **Authentication** → Email/Contraseña
3. Crear base de datos **Firestore** (modo producción)
4. Habilitar **Storage**
5. Descargar `google-services.json` → colocar en `app/google-services.json`
6. Aplicar las reglas de seguridad (ver sección 6)

### Paso 3: Índices Firestore

Crear los siguientes índices compuestos en Firestore Console → Índices:

| Colección | Campos | Orden |
|-----------|--------|-------|
| `swipes` | `swiperId` ASC, `sockOwnerId` ASC, `direction` ASC | - |
| `matches` | `user1Id` ASC, `createdAt` DESC | - |
| `matches` | `user2Id` ASC, `createdAt` DESC | - |

### Paso 4: Build y Run

```bash
./gradlew assembleDebug
```

O usar el botón Run en Android Studio con un emulador/dispositivo físico (API 24+).

> **Nota Windows**: `./gradlew` desde terminal puede fallar con `Unable to establish loopback connection` si el firewall bloquea `127.0.0.1`. Compila siempre desde Android Studio → **Build > Make Project** para evitar este problema del sistema operativo.

### Paso 5: Verificar ML Kit

El modelo de detección de caras se descarga automáticamente la primera vez que se usa. Requiere conexión a Internet. En el emulador, asegurarse de tener Google Play Services instalado.

---

## 12. Deuda Técnica y Roadmap

### Deuda Técnica Actual

| ID | Descripción | Severidad | Estado |
|----|-------------|-----------|--------|
| DT-D | Aplicar reglas Firebase en consola (acción manual) | Alta | **Pendiente (acción manual)** |
| DT-E | Sin tests unitarios ni de UI | Media | ✅ Resuelto — 17 tests JVM + 3 instrumentados |
| DT-F | `activity_main.xml` eliminado | Baja | ✅ Resuelto |
| DT-G | Sin DI — repositorios duplicados | Media | ✅ Resuelto — Hilt migrado (DT-010) |
| DT-H | `getSocksToSwipe` no reactivo ante nuevos swipes en sesión | Baja | Abierta |
| DT-I | Sin paginación en socks (carga todos en memoria) | Baja (MVP) | Abierta |
| DT-J | Detección de match sin transacción atómica | Baja (MVP) | Abierta |
| DT-K | Sin manejo de estado offline | Baja | Abierta |

### Roadmap Sugerido

#### Fase 1 (MVP funcional) — Completada
- [x] Autenticación Firebase
- [x] Subida de calcetines con detección de caras
- [x] Swipe con animación
- [x] Detección de matches
- [x] Pantalla de matches
- [x] Reglas de seguridad

#### Fase 2 (Pulido) — Completada
- [x] Hilt DI — grafo completo con @Singleton repositorios y @HiltViewModel
- [x] Tests unitarios (MockK) — AuthViewModel, UploadViewModel, SwipeUiState, SatiricCopy
- [x] Test instrumentado con Hilt — SwipeViewModelTest + HiltTestRunner
- [x] FaceDetector como interfaz inyectable — desacopla ML Kit del ViewModel
- [x] Animación Lottie confetti en overlay de match
- [x] Pantalla de perfil con logout (popUpTo(0) limpia backstack)
- [x] Firebase rules corregidas — swipes read + matches create tightened

#### Fase 3 (Escala)
- [ ] Paginación con Paging 3 para socks
- [ ] Cloud Functions para detección de match (transacción atómica)
- [ ] Push notifications (FCM) para nuevo match
- [ ] Índices Firestore optimizados

---

## 13. Preguntas Frecuentes Técnicas

**¿Por qué el proyecto se llama `calcetinder_prueba` y no `calcetinder`?**
El proyecto de trabajo activo es `calcetinder_prueba` (AGP 9.1.0, Kotlin 2.1.0, compileSdk 36). El proyecto original `calcetinder` es el template/referencia de arquitectura con Groovy DSL y AGP 8.1.1. Ambos coexisten en el directorio `DAM/`.

**¿Qué pasa si ML Kit no detecta una cara que sí existe?**
Los falsos negativos son posibles (caras muy pequeñas, anguladas o con baja luz). El resultado es que la imagen se sube sin penalización. Para un proyecto satírico educativo, esto es aceptable y hasta divertido.

**¿Puedo usar la app sin conexión a Internet?**
Parcialmente. El login y la subida de calcetines requieren conexión (Firebase). Firestore tiene cache local que podría mostrar datos previos en modo offline, pero no está configurado explícitamente en este MVP.

**¿Por qué `allowBackup="false"` en el Manifest?**
Para evitar que backups automáticos de Android incluyan datos sensibles del usuario (tokens de sesión, etc.). La opción estándar `allowBackup="true"` en apps con auth es un riesgo de privacidad.

**¿Cómo funciona el `sealed class Screen`?**
Permite refactoring seguro: si cambia una ruta, el compilador detecta todos los usos no actualizados. Alternativa moderna: Navigation Destinations type-safe con Kotlin Serializable (disponible desde Navigation 2.8.0), que es el siguiente paso en el roadmap.
