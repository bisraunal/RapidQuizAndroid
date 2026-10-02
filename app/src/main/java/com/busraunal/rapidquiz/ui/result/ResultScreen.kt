package com.busraunal.rapidquiz.ui.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.busraunal.rapidquiz.data.model.QuizSubmitRequest
import com.busraunal.rapidquiz.data.model.QuizSubmitResponse
import com.busraunal.rapidquiz.data.repository.QuizRepository
import com.busraunal.rapidquiz.ui.components.AnswerReview
import com.busraunal.rapidquiz.ui.components.LeaderboardTable
import com.busraunal.rapidquiz.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    categoryName: String,
    categorySlug: String,
    totalQuestions: Int,
    answeredCount: Int,
    emptyCount: Int,
    totalTimeTaken: Double,
    submitRequest: QuizSubmitRequest,
    onPlayAgain: () -> Unit,
    onChooseCategory: () -> Unit
) {
    val repository = remember { QuizRepository() }
    val coroutineScope = rememberCoroutineScope()

    var playerName by remember { mutableStateOf(submitRequest.playerName) }
    var inputError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitResponse by remember { mutableStateOf<QuizSubmitResponse?>(null) }
    var activeTab by remember { mutableStateOf("leaderboard") } // "leaderboard" or "review"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quiz Sonucu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Greeting (Trophy banner)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFBBF24), AmberGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFF030712),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tebrikler! Quiz Tamamlandı",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$categoryName kategorisinde $totalQuestions soruyu tamamladın.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. Stats Grid (4 Cards: Cevaplanan, Toplam Süre, Süresi Dolan, +Hız Bonusu)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        icon = Icons.Default.Bolt,
                        value = "$answeredCount",
                        label = "Cevaplanan",
                        tint = CyanLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        icon = Icons.Default.Schedule,
                        value = "${totalTimeTaken}s",
                        label = "Toplam Süre",
                        tint = SkyAccent,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        icon = Icons.Default.RemoveCircleOutline,
                        value = "$emptyCount",
                        label = "Süresi Dolan",
                        tint = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        icon = Icons.Default.AutoAwesome,
                        value = "+Bonus",
                        label = "Hız Puanı",
                        tint = PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. State 1: Nickname Input Card (Before Submit)
            if (submitResponse == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Skorunu Kaydet & Lider Tablosuna Gir!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Adını veya takma adını girerek puanını skorborda yazdır ve doğru/yanlış cevaplarını incele.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = playerName,
                                onValueChange = {
                                    playerName = it
                                    inputError = null
                                },
                                placeholder = { Text("Örn: EfsaneYazilimci") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanLight,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = BackgroundDark,
                                    unfocusedContainerColor = BackgroundDark
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (inputError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = inputError ?: "",
                                    color = WrongRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (playerName.trim().length < 2) {
                                        inputError = "Takma ad en az 2 karakter olmalıdır."
                                    } else {
                                        isSubmitting = true
                                        inputError = null
                                        coroutineScope.launch {
                                            val req = submitRequest.copy(playerName = playerName.trim())
                                            repository.submitQuiz(req)
                                                .onSuccess { res ->
                                                    submitResponse = res
                                                    isSubmitting = false
                                                }
                                                .onFailure { err ->
                                                    inputError = err.localizedMessage ?: "Skor kaydedilemedi."
                                                    isSubmitting = false
                                                }
                                        }
                                    }
                                },
                                enabled = !isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary
                                )
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color(0xFF030712),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Skorunu Gönder & Sonuçları Gör ➔",
                                        color = Color(0xFF030712),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 4. State 2: Player Highlight Banner (After Submit)
                item {
                    val res = submitResponse!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.5.dp, CyanLight.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CyanLight.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, CyanLight.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "🏆 Derecen: ${res.rank}. Sıra",
                                    color = CyanLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${res.totalScore} PUAN",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "✓ ${res.correctCount} Doğru",
                                    color = CorrectGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(text = "•", color = TextMuted)
                                Text(
                                    text = "✗ ${res.wrongCount} Yanlış",
                                    color = WrongRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(text = "•", color = TextMuted)
                                Text(
                                    text = "⏱ ${res.totalTimeTaken}s",
                                    color = SkyAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // 5. Tabs (Lider Tablosu vs Cevapları İncele)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TabButton(
                            title = "Lider Tablosu (Top 10)",
                            icon = Icons.Default.FormatListNumbered,
                            isSelected = activeTab == "leaderboard",
                            selectedColor = AmberGold,
                            onClick = { activeTab = "leaderboard" },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            title = "Cevapları İncele",
                            icon = Icons.Default.MenuBook,
                            isSelected = activeTab == "review",
                            selectedColor = CyanLight,
                            onClick = { activeTab = "review" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 6. Tab Content
                if (activeTab == "leaderboard") {
                    item {
                        LeaderboardTable(
                            scores = submitResponse?.top10 ?: emptyList(),
                            highlightPlayerName = submitResponse?.playerName
                        )
                    }
                } else {
                    item {
                        AnswerReview(
                            breakdown = submitResponse?.resultsBreakdown ?: emptyList()
                        )
                    }
                }
            }

            // 7. Action Buttons Footer (Yeniden Oyna & Farklı Kategori Seç)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.RotateLeft, contentDescription = null, tint = Color(0xFF030712))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Yeniden Oyna", color = Color(0xFF030712), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onChooseCategory,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(text = "Farklı Kategori Seç", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) selectedColor else SurfaceDark,
        border = BorderStroke(1.dp, if (isSelected) selectedColor else CardBorder),
        modifier = modifier.height(42.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF030712) else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) Color(0xFF030712) else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
            )
        }
    }
}
