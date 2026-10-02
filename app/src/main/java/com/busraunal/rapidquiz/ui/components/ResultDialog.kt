package com.busraunal.rapidquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.busraunal.rapidquiz.data.model.QuestionResultDto
import com.busraunal.rapidquiz.data.model.QuizSubmitResponse
import com.busraunal.rapidquiz.ui.theme.*

@Composable
fun ResultDialog(
    totalQuestions: Int,
    answeredCount: Int,
    emptyCount: Int,
    totalTimeTaken: Double,
    isSubmitting: Boolean,
    submitResponse: QuizSubmitResponse?,
    onSubmit: (playerName: String) -> Unit,
    onViewLeaderboard: () -> Unit,
    onPlayAgain: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBreakdown by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(if (submitResponse != null) 0.88f else 0.65f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (submitResponse == null) {
                    // ADIM 1: Quiz Bitti - İsim Girişi ve Ön Özet
                    Text(
                        text = "🏁 Quiz Tamamlandı!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ön Özet Bilgileri
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "Toplam Soru", value = "$totalQuestions", color = TextPrimary)
                        StatItem(label = "Cevaplanan", value = "$answeredCount", color = NeonCyan)
                        StatItem(label = "Boş Geçilen", value = "$emptyCount", color = TextMuted)
                        StatItem(label = "Toplam Süre", value = "${totalTimeTaken}s", color = NeonAmber)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = playerName,
                        onValueChange = {
                            playerName = it
                            errorMessage = null
                        },
                        label = { Text("Oyuncu / Takma Adınız") },
                        placeholder = { Text("Örn: HızlıCevapçı") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = NeonCyan,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = WrongRed,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (playerName.trim().length < 2) {
                                errorMessage = "Lütfen en az 2 karakterli bir isim giriniz."
                            } else {
                                onSubmit(playerName.trim())
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = BackgroundDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Skoru Hesapla & Sıranı Gör ➔",
                                color = BackgroundDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    // ADIM 2: Doğrulanan Gerçek Doğru/Yanlış ve Detaylı Analiz
                    Text(
                        text = "🎉 Tebrikler, ${submitResponse.playerName}!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Skor & Sıralama Kartı
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardDark,
                        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOPLAM PUAN",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${submitResponse.totalScore}",
                                color = NeonPurple,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Kategori Sıralamanız: #${submitResponse.rank}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = CardBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Gerçek Doğru / Yanlış / Boş / Süre
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(label = "Doğru", value = "${submitResponse.correctCount}", color = CorrectGreen)
                                StatItem(label = "Yanlış", value = "${submitResponse.wrongCount}", color = WrongRed)
                                StatItem(label = "Boş", value = "${submitResponse.emptyCount}", color = TextMuted)
                                StatItem(label = "Süre", value = "${submitResponse.totalTimeTaken}s", color = NeonAmber)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detaylı Soru Listesi (Doğru / Yanlış Analizi)
                    val breakdown = submitResponse.resultsBreakdown ?: emptyList()
                    if (breakdown.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📝 Soru ve Cevap Analizi (${breakdown.size} Soru)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(breakdown) { index, item ->
                                QuestionReviewItem(index = index + 1, result = item)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Alt Butonlar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPlayAgain,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text("Tekrar Oyna", color = TextPrimary, fontSize = 13.sp)
                        }

                        Button(
                            onClick = onViewLeaderboard,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Lider Tablosu", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionReviewItem(
    index: Int,
    result: QuestionResultDto
) {
    val isCorrect = result.isCorrect
    val statusColor = if (isCorrect) CorrectGreen else WrongRed
    val bgColor = if (isCorrect) CorrectGreen.copy(alpha = 0.08f) else WrongRed.copy(alpha = 0.08f)

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Soru $index",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Text(
                    text = if (isCorrect) "+${result.earnedPoints} Puan (${result.timeTaken}s)" else "${result.timeTaken}s",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result.questionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isCorrect) {
                Text(
                    text = "✓ Doğru Cevabınız: ${result.correctChoiceText}",
                    fontSize = 12.sp,
                    color = CorrectGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Column {
                    Text(
                        text = "✗ Sizin Seçiminiz: ${result.selectedChoiceText ?: 'Boş Bırakıldı'}",
                        fontSize = 12.sp,
                        color = WrongRed
                    )
                    Text(
                        text = "✓ Doğru Cevap: ${result.correctChoiceText}",
                        fontSize = 12.sp,
                        color = CorrectGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
