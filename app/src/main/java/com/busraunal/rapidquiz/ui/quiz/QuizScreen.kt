package com.busraunal.rapidquiz.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.busraunal.rapidquiz.ui.components.ChoiceButton
import com.busraunal.rapidquiz.ui.components.CountdownTimerBar
import com.busraunal.rapidquiz.ui.components.ResultDialog
import com.busraunal.rapidquiz.ui.theme.*
import com.busraunal.rapidquiz.util.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    categorySlug: String,
    onBackToCategories: () -> Unit,
    onViewLeaderboard: () -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (val state = uiState) {
                        is QuizUiState.Playing -> state.categoryData.category
                        is QuizUiState.Finished -> state.categoryData.category
                        else -> "Quiz"
                    }
                    Text(
                        text = titleText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
                            tint = if (isMuted) TextMuted else NeonCyan
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
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Sorular hazırlanıyor...", color = TextSecondary)
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
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Kategorilere Dön", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is QuizUiState.Playing -> {
                    // Start category background music if not started
                    LaunchedEffect(state.categoryData.musicUrl) {
                        soundManager.playMusic(state.categoryData.musicUrl)
                    }

                    val question = state.categoryData.questions[state.currentQuestionIndex]
                    val themeColor = getCategoryThemeColor(state.categoryData.colorTheme)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Header row: Question index & Points badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soru ${state.currentQuestionIndex + 1} / ${state.categoryData.totalQuestions}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CardDark,
                                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "+${question.points} Puan",
                                    color = themeColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 5s Countdown Bar
                        CountdownTimerBar(
                            remainingSeconds = state.remainingSeconds,
                            totalSeconds = 5.0f
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = question.text,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    lineHeight = 24.sp
                                )

                                if (!question.codeSnippet.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BackgroundDark)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = question.codeSnippet,
                                            color = NeonCyan,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Choice Buttons
                        question.choices.forEachIndexed { index, choice ->
                            ChoiceButton(
                                index = index,
                                choice = choice,
                                isSelected = state.selectedChoiceId == choice.id,
                                themeColor = themeColor,
                                onClick = {
                                    viewModel.onChoiceSelected(choice.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    // Show Completion Modal Dialog
                    ResultDialog(
                        totalQuestions = state.categoryData.totalQuestions,
                        answeredCount = state.answers.count { it.selectedChoiceId != null },
                        emptyCount = state.emptyCount,
                        totalTimeTaken = state.totalTimeTaken,
                        isSubmitting = state.isSubmitting,
                        submitResponse = state.submitResponse,
                        onSubmit = { name -> viewModel.submitQuiz(name) },
                        onViewLeaderboard = onViewLeaderboard,
                        onPlayAgain = { viewModel.loadQuestions(categorySlug) }
                    )
                }
            }
        }
    }
}
