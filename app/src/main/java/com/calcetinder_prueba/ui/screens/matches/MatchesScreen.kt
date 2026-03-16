package com.calcetinder_prueba.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.calcetinder_prueba.data.model.Match
import com.calcetinder_prueba.ui.theme.CalcetinderPink
import com.calcetinder_prueba.ui.theme.LikeGreen
import com.calcetinder_prueba.util.SatiricCopy

@Composable
fun MatchesScreen(viewModel: MatchesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAFD))
            .systemBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = SatiricCopy.MATCHES_TITLE,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = CalcetinderPink,
                letterSpacing = 2.sp
            )
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CalcetinderPink)
                }
            }
            state.matches.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = SatiricCopy.MATCHES_EMPTY_TITLE,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalcetinderPink
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = SatiricCopy.MATCHES_EMPTY_BODY,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    items(state.matches, key = { it.id }) { match ->
                        MatchCard(match)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MatchCard(match: Match) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Indicador de match
            Text(
                text = SatiricCopy.MATCH_FOUND_TITLE,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LikeGreen,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Los dos calcetines emparejados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchSockThumbnail(
                    imageUrl = match.sock1ImageUrl,
                    name = match.sock1Name.ifBlank { SatiricCopy.SOCK_NAME_PLACEHOLDER },
                    modifier = Modifier.weight(1f)
                )
                MatchSockThumbnail(
                    imageUrl = match.sock2ImageUrl,
                    name = match.sock2Name.ifBlank { SatiricCopy.SOCK_NAME_PLACEHOLDER },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Línea de compatibilidad satírica
            Text(
                text = SatiricCopy.matchCompatibility(),
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MatchSockThumbnail(imageUrl: String, name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0E8F0))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A0A12),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
