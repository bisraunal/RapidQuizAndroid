package com.busraunal.rapidquiz.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.busraunal.rapidquiz.data.model.AnswerSubmissionDto
import com.busraunal.rapidquiz.data.model.ChoiceDto
import com.busraunal.rapidquiz.data.model.QuizSubmitRequest
import com.busraunal.rapidquiz.ui.components.TimerBar
import com.busraunal.rapidquiz.ui.theme.*
import com.busraunal.rapidquiz.util.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    categorySlug: String,
    onBackToCategories: () -> Unit,
    onQuizCompleted: (
        categoryName: String,
        categorySlug: String,
        totalQuestions: Int,
        answeredCount: Int,
        emptyCount: Int,
        totalTimeTaken: Double,
        submitRequest: QuizSubmitRequest
    ) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    var isMuted by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categorySlug) {
        viewModel.loadQuestions(categorySlug)
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopMusic()
        }
    }

    // Handle transition to ResultScreen when finished
    LaunchedEffect(uiState) {
        val state = uiState
        if (state is QuizUiState.Finished) {
            val answered = state.answers.count { it.selectedChoiceId != null }
            val submitReq = QuizSubmitRequest(
                categorySlug = state.categoryData.categorySlug,
                playerName = "",
                answers = state.answers.map {
                    AnswerSubmissionDto(
                        questionId = it.questionId,
                        selectedChoiceId = it.selectedChoiceId,
                        timeTaken = it.timeTaken
                    )
                }
            )

            onQuizCompleted(
                state.categoryData.category,
                state.categoryData.categorySlug,
                state.categoryData.totalQuestions,
                answered,
                state.emptyCount,
                state.totalTimeTaken,
                submitReq
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val catName = (uiState as? QuizUiState.Playing)?.categoryData?.category ?: "Quiz"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = CyanLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "KATEGORİ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = catName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToCategories) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isMuted = soundManager.toggleMute() }) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Ses",
                            tint = if (isMuted) TextMuted else CyanLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = CyanLight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Sorular hazırlanıyor...", color = TextSecondary, fontSize = 14.sp)
                    }
                }
                is QuizUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Hata", color = WrongRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.message, color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBackToCategories,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                        ) {
                            Text("Kategorilere Dön", color = Color(0xFF030712), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is QuizUiState.Playing -> {
                    // Play category music
                    LaunchedEffect(state.categoryData.musicUrl) {
                        soundManager.playMusic(state.categoryData.musicUrl)
                    }

                    val question = state.categoryData.questions[state.currentQuestionIndex]
                    val progressPercent = ((state.currentQuestionIndex + 1).toFloat() / state.categoryData.totalQuestions.toFloat())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // 5s Countdown Bar
                        TimerBar(
                            timeRemaining = state.remainingSeconds,
                            maxTime = 5.0f
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SurfaceDark)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progressPercent)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(CyanLight)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Question Card (Matching QuestionCard.vue)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                // Question index & points row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = CardDark,
                                        border = BorderStroke(1.dp, CardBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(CyanLight)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Soru ${state.currentQuestionIndex + 1} / ${state.categoryData.totalQuestions}",
                                                color = CyanLight,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = "+${question.points} Puan",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Question text
                                Text(
                                    text = question.text,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    lineHeight = 26.sp
                                )

                                // Code snippet
                                if (!question.codeSnippet.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(BackgroundDark)
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = question.codeSnippet,
                                            color = CyanLight,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Choices Grid
                        question.choices.forEachIndexed { index, choice ->
                            val isSelected = state.selectedChoiceId == choice.id
                            QuizChoiceBox(
                                index = index,
                                choice = choice,
                                isSelected = isSelected,
                                isAnswerLocked = state.isAnswerLocked,
                                onClick = {
                                    if (!state.isAnswerLocked) {
                                        viewModel.onChoiceSelected(choice.id)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    // Loading while transitioning
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizChoiceBox(
    index: Int,
    choice: ChoiceDto,
    isSelected: Boolean,
    isAnswerLocked: Boolean,
    onClick: () -> Unit
) {
    val choiceLetter = ('A' + index).toString()

    val cardBg = when {
        isSelected -> CyanPrimary.copy(alpha = 0.2f)
        isAnswerLocked -> SurfaceDark.copy(alpha = 0.5f)
        else -> SurfaceDark
    }

    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, CyanLight)
        else -> BorderStroke(1.dp, CardBorder)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswerLocked) { onClick() },
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
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) CyanLight else CardDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = choiceLetter,
                    color = if (isSelected) Color(0xFF030712) else TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = choice.text,
                color = if (isSelected) TextWhite else TextPrimary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
