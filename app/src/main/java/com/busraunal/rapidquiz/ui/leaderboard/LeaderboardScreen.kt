package com.busraunal.rapidquiz.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.busraunal.rapidquiz.data.model.ScoreDto
import com.busraunal.rapidquiz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Lider Tablosu (Top 10)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Scrollable Tabs
            ScrollableTabRow(
                selectedTabIndex = viewModel.tabs.indexOf(selectedTab),
                containerColor = SurfaceDark,
                contentColor = NeonCyan,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = CardBorder) }
            ) {
                viewModel.tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (val state = uiState) {
                    is LeaderboardUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = NeonCyan)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Sıralama yükleniyor...", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                    is LeaderboardUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Yüklenemedi", color = WrongRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = state.message, color = TextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.loadLeaderboard(selectedTab.slug) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BackgroundDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Yenile", color = BackgroundDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    is LeaderboardUiState.Success -> {
                        if (state.scores.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Bu kategoride henüz skor kaydı yok.",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "İlk rekoru sen kır!",
                                    color = NeonCyan,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                itemsIndexed(state.scores, key = { _, item -> item.id }) { index, score ->
                                    LeaderboardRowItem(rank = index + 1, score = score)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRowItem(
    rank: Int,
    score: ScoreDto
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> TextSecondary
    }

    val rankIcon = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rank <= 3) CardDark else SurfaceDark
        ),
        border = BorderStroke(1.dp, if (rank == 1) rankColor.copy(alpha = 0.5f) else CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (rank <= 3) rankColor.copy(alpha = 0.15f) else CardDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankIcon,
                    color = rankColor,
                    fontSize = if (rank <= 3) 18.sp else 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Info & Stats
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = score.playerName,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!score.categoryName.isNullOrBlank()) {
                        Text(
                            text = score.categoryName,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = " • ", color = TextMuted, fontSize = 11.sp)
                    }
                    Text(
                        text = "✓${score.correctCount} ✗${score.wrongCount} • ${score.totalTimeTaken}s",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Total Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${score.totalScore}",
                    color = if (rank == 1) NeonCyan else NeonPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Puan",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
