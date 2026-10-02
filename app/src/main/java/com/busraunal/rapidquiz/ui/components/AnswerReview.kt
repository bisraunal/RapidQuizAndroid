package com.busraunal.rapidquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.busraunal.rapidquiz.data.model.QuestionResultDto
import com.busraunal.rapidquiz.ui.theme.*

@Composable
fun AnswerReview(
    breakdown: List<QuestionResultDto>,
    modifier: Modifier = Modifier
) {
    if (breakdown.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "İncelenecek soru verisi bulunamadı.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(breakdown, key = { _, item -> item.questionId }) { index, item ->
            AnswerReviewItem(index = index + 1, result = item)
        }
    }
}

@Composable
fun AnswerReviewItem(
    index: Int,
    result: QuestionResultDto
) {
    val isCorrect = result.isCorrect
    val statusColor = if (isCorrect) CorrectGreen else WrongRed
    val bgColor = if (isCorrect) CorrectGreen.copy(alpha = 0.06f) else WrongRed.copy(alpha = 0.06f)
    val borderColor = if (isCorrect) CorrectGreen.copy(alpha = 0.25f) else WrongRed.copy(alpha = 0.25f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Question index badge & Status (Doğru / Yanlış / Puan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Soru $index",
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardDark,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = if (isCorrect) "+${result.earnedPoints} Puan • ${result.timeTaken}s" else "0 Puan • ${result.timeTaken}s",
                        color = if (isCorrect) NeonCyan else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = result.questionText,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Choice details box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = bgColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    if (isCorrect) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✓ Doğru Cevabınız: ",
                                color = CorrectGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = result.correctChoiceText ?: "",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        val chosen = result.selectedChoiceText ?: "Boş Bırakıldı (Süre Doldu)"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✗ Sizin Seçiminiz: ",
                                color = WrongRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = chosen,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✓ Doğru Cevap: ",
                                color = CorrectGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = result.correctChoiceText ?: "",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
