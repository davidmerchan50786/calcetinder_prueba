package com.calcetinder_prueba.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calcetinder_prueba.ui.theme.CalcetinderPink
import com.calcetinder_prueba.util.SatiricCopy

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val loggedOut by viewModel.loggedOut.collectAsState()

    LaunchedEffect(loggedOut) {
        if (loggedOut) onLogout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))   // Fondo claro — consistente con SwipeScreen
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text          = SatiricCopy.PROFILE_TITLE,
            fontSize      = 28.sp,
            fontWeight    = FontWeight.Black,
            color         = CalcetinderPink,
            letterSpacing = 3.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text      = SatiricCopy.PROFILE_SUBTITLE,
            fontSize  = 13.sp,
            color     = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Avatar anónimo (coherente con la filosofía anti-ego de la app)
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, CalcetinderPink.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de datos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            ProfileRow(label = "EMAIL", value = viewModel.userEmail.ifBlank { "—" }, valueMaxLines = 1)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
            ProfileRow(label = "ID DE PORTADOR", value = viewModel.userIdShort)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
            ProfileRow(
                label      = SatiricCopy.PROFILE_ROLE_LABEL,
                value      = SatiricCopy.PROFILE_ROLE_VALUE,
                valueColor = CalcetinderPink
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nota satírica
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF0F0), RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(
                text      = SatiricCopy.PROFILE_LOGOUT_CONFIRM,
                fontSize  = 12.sp,
                color     = Color(0xFF9CA3AF),
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón logout con borde rojo sutil
        Button(
            onClick = { viewModel.logout() },
            colors  = ButtonDefaults.buttonColors(
                containerColor = CalcetinderPink,
                contentColor   = Color.White
            ),
            shape    = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(
                text       = SatiricCopy.PROFILE_LOGOUT_BTN,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF374151),
    valueMaxLines: Int = 2
) {
    Column {
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = Color(0xFF9CA3AF),
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = value,
            fontSize   = 14.sp,
            color      = valueColor,
            fontWeight = FontWeight.Medium,
            maxLines   = valueMaxLines,
            overflow   = TextOverflow.Ellipsis,
            lineHeight = 20.sp
        )
    }
}
