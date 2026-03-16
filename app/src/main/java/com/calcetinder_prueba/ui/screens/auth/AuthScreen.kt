package com.calcetinder_prueba.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calcetinder_prueba.ui.components.CalcetinderLogo
import com.calcetinder_prueba.ui.theme.CalcetinderOrange
import com.calcetinder_prueba.ui.theme.CalcetinderPink
import com.calcetinder_prueba.ui.theme.PoppinsFamily
import com.calcetinder_prueba.util.SatiricCopy

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Si ya está logueado, pasar directamente
    LaunchedEffect(Unit) {
        if (viewModel.isAlreadyLoggedIn) onAuthSuccess()
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthSuccess()
    }

    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        // ── TOP: gradiente llama naranja→rojo, idéntico al estilo Tinder ─
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CalcetinderOrange, CalcetinderPink)
                    )
                )
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CalcetinderLogo(sizeDp = 88.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text          = SatiricCopy.APP_NAME,
                    fontFamily    = PoppinsFamily,
                    fontSize      = 40.sp,
                    fontWeight    = FontWeight.Black,
                    color         = Color.White,
                    letterSpacing = 5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text      = SatiricCopy.AUTH_TAGLINE,
                    fontFamily = PoppinsFamily,
                    fontSize   = 13.sp,
                    color      = Color.White.copy(alpha = 0.85f),
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.padding(horizontal = 40.dp)
                )
            }
        }

        // ── BOTTOM: tarjeta blanca con bordes redondeados superiores ─────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = if (isRegister) "Crear cuenta" else "Iniciar sesión",
                    fontFamily = PoppinsFamily,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text      = if (isRegister) "Únete a la comunidad más rara de calcetines"
                                else            "Tus calcetines te echan de menos",
                    fontFamily = PoppinsFamily,
                    fontSize   = 13.sp,
                    color      = Color(0xFF9CA3AF),
                    textAlign  = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; viewModel.resetState() },
                    label         = { Text("Email", fontFamily = PoppinsFamily) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction    = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors   = lightFieldColors(),
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it; viewModel.resetState() },
                    label         = { Text("Contraseña", fontFamily = PoppinsFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (isRegister) viewModel.signUp(email, password)
                            else            viewModel.signIn(email, password)
                        }
                    ),
                    colors   = lightFieldColors(),
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Botón con gradiente horizontal
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (isRegister) viewModel.signUp(email, password)
                        else            viewModel.signIn(email, password)
                    },
                    enabled        = uiState !is AuthUiState.Loading,
                    colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape          = RoundedCornerShape(14.dp),
                    modifier       = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(CalcetinderPink, CalcetinderOrange)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text      = if (isRegister) SatiricCopy.AUTH_REGISTER_BTN
                                            else           SatiricCopy.AUTH_LOGIN_BTN,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp,
                                color      = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { isRegister = !isRegister; viewModel.resetState() }) {
                    Text(
                        text      = if (isRegister) SatiricCopy.AUTH_TOGGLE_TO_LOGIN
                                    else            SatiricCopy.AUTH_TOGGLE_TO_REGISTER,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize   = 13.sp,
                        color      = CalcetinderPink
                    )
                }

                if (uiState is AuthUiState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text      = (uiState as AuthUiState.Error).message,
                            fontFamily = PoppinsFamily,
                            fontSize   = 13.sp,
                            color      = CalcetinderPink,
                            textAlign  = TextAlign.Center,
                            modifier   = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text      = SatiricCopy.AUTH_FOOTER,
                    fontFamily = PoppinsFamily,
                    fontSize   = 11.sp,
                    color      = Color(0xFFD1D5DB),
                    textAlign  = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun lightFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = CalcetinderPink,
    unfocusedBorderColor    = Color(0xFFE5E7EB),
    focusedLabelColor       = CalcetinderPink,
    unfocusedLabelColor     = Color(0xFF9CA3AF),
    focusedTextColor        = Color(0xFF111827),
    unfocusedTextColor      = Color(0xFF374151),
    cursorColor             = CalcetinderPink,
    focusedContainerColor   = Color.White,
    unfocusedContainerColor = Color(0xFFFAFAFA),
)
