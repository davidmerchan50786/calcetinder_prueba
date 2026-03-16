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
import com.calcetinder_prueba.ui.theme.CalcetinderPinkLight
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
            .background(Color(0xFF0F0F0F))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Título
        Text(
            text = SatiricCopy.PROFILE_TITLE,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = CalcetinderPink,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = SatiricCopy.PROFILE_SUBTITLE,
            fontSize = 13.sp,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Avatar — icono anónimo, sin foto (coherente con la filosofía anti-ego)
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFF1A1A1A), CircleShape)
                .border(2.dp, CalcetinderPink.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF444444),
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de datos del usuario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            ProfileRow(
                label = "EMAIL",
                value = viewModel.userEmail.ifBlank { "—" },
                valueMaxLines = 1
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFF2A2A2A)
            )
            ProfileRow(
                label = "ID DE PORTADOR",
                value = viewModel.userIdShort
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFF2A2A2A)
            )
            ProfileRow(
                label = SatiricCopy.PROFILE_ROLE_LABEL,
                value = SatiricCopy.PROFILE_ROLE_VALUE,
                valueColor = CalcetinderPink.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Nota satírica
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0D0D), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(
                text = SatiricCopy.PROFILE_LOGOUT_CONFIRM,
                fontSize = 12.sp,
                color = Color(0xFF555555),
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón logout
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A0A0A),
                contentColor = Color(0xFFFF6B6B)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = SatiricCopy.PROFILE_LOGOUT_BTN,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFFCCCCCC),
    valueMaxLines: Int = 2
) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF555555),
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            maxLines = valueMaxLines,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 20.sp
        )
    }
}
