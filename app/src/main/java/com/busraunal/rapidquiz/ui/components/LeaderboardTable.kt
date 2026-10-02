package com.busraunal.rapidquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.busraunal.rapidquiz.data.model.ScoreDto
import com.busraunal.rapidquiz.ui.theme.*

@Composable
fun LeaderboardTable(
    scores: List<ScoreDto>,
    highlightPlayerName: String? = null,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bu kategoride henüz skor kaydı bulunmuyor.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "İlk rekoru sen kır!",
                    color = CyanLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(scores, key = { _, item -> item.id }) { index, score ->
            val rank = index + 1
            val isHighlighted = highlightPlayerName != null &&
                    score.playerName.equals(highlightPlayerName, ignoreCase = true)

            LeaderboardRow(
                rank = rank,
                score = score,
                isHighlighted = isHighlighted
            )
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    score: ScoreDto,
    isHighlighted: Boolean
) {
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFF59E0B) // Gold
        2 -> Color(0xFF94A3B8) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> TextSecondary
    }

    val rankIcon = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    val cardBg = when {
        isHighlighted -> CardDark
        rank == 1 -> Color(0xFF1E293B)
        else -> SurfaceDark
    }

    val borderStroke = when {
        isHighlighted -> BorderStroke(2.dp, CyanLight)
        rank == 1 -> BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
        else -> BorderStroke(1.dp, CardBorder)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Icon / Number
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (rank <= 3) rankBadgeColor.copy(alpha = 0.15f)
                        else CardDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankIcon,
                    fontSize = if (rank <= 3) 18.sp else 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = rankBadgeColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = score.playerName,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isHighlighted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanLight.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, CyanLight.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "SEN",
                                color = CyanLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!score.categoryName.isNullOrBlank()) {
                        Text(
                            text = score.categoryName,
                            color = CyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = " • ", color = TextMuted, fontSize = 11.sp)
                    }

                    Text(
                        text = "✓${score.correctCount} ✗${score.wrongCount} • ⏱ ${score.totalTimeTaken}s",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Total Score Pill
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${score.totalScore}",
                    color = if (rank == 1) AmberGold else CyanLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "PUAN",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
