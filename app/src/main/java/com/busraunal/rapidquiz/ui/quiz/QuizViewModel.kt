package com.busraunal.rapidquiz.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busraunal.rapidquiz.data.model.*
import com.busraunal.rapidquiz.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserAnswer(
    val questionId: String,
    val selectedChoiceId: String?,
    val timeTaken: Double
)

sealed interface QuizUiState {
    object Loading : QuizUiState
    data class Error(val message: String) : QuizUiState
    data class Playing(
        val categoryData: CategoryQuestionsResponse,
        val currentQuestionIndex: Int,
        val remainingSeconds: Float,
        val selectedChoiceId: String?,
        val isAnswerLocked: Boolean
    ) : QuizUiState
    data class Finished(
        val categoryData: CategoryQuestionsResponse,
        val answers: List<UserAnswer>,
        val correctEstimated: Int,
        val wrongEstimated: Int,
        val emptyCount: Int,
        val totalTimeTaken: Double,
        val isSubmitting: Boolean,
        val submitResponse: QuizSubmitResponse?,
        val submitError: String?
    ) : QuizUiState
}

class QuizViewModel(
    private val repository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var questionsResponse: CategoryQuestionsResponse? = null
    private val recordedAnswers = mutableListOf<UserAnswer>()
    private var timerJob: Job? = null

    private var currentQuestionStartTime = 0L

    fun loadQuestions(slug: String) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            recordedAnswers.clear()
            repository.getCategoryQuestions(slug)
                .onSuccess { response ->
                    questionsResponse = response
                    if (response.questions.isNotEmpty()) {
                        startQuestion(0)
                    } else {
                        _uiState.value = QuizUiState.Error("Bu kategoride henüz soru bulunmuyor.")
                    }
                }
                .onFailure { error ->
                    _uiState.value = QuizUiState.Error(
                        error.localizedMessage ?: "Sorular yüklenirken hata oluştu."
                    )
                }
        }
    }

    private fun startQuestion(index: Int) {
        val data = questionsResponse ?: return
        if (index >= data.questions.size) {
            finishQuiz()
            return
        }

        currentQuestionStartTime = System.currentTimeMillis()

        _uiState.value = QuizUiState.Playing(
            categoryData = data,
            currentQuestionIndex = index,
            remainingSeconds = 5.0f,
            selectedChoiceId = null,
            isAnswerLocked = false
        )

        startTimer(index)
    }

    private fun startTimer(index: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var timeLeft = 5.0f
            while (timeLeft > 0f) {
                delay(100)
                timeLeft -= 0.1f
                val clamped = if (timeLeft < 0f) 0f else timeLeft

                val currentState = _uiState.value
                if (currentState is QuizUiState.Playing && currentState.currentQuestionIndex == index) {
                    if (!currentState.isAnswerLocked) {
                        _uiState.value = currentState.copy(remainingSeconds = clamped)
                    }
                }
            }

            // Time expired without answer
            val currentState = _uiState.value
            if (currentState is QuizUiState.Playing && currentState.currentQuestionIndex == index && !currentState.isAnswerLocked) {
                recordAndNext(choiceId = null, timeTaken = 5.0)
            }
        }
    }

    fun onChoiceSelected(choiceId: String) {
        val currentState = _uiState.value as? QuizUiState.Playing ?: return
        if (currentState.isAnswerLocked) return

        timerJob?.cancel()

        val elapsedMillis = System.currentTimeMillis() - currentQuestionStartTime
        val timeTakenSeconds = (elapsedMillis / 1000.0).coerceIn(0.1, 5.0)

        _uiState.value = currentState.copy(
            selectedChoiceId = choiceId,
            isAnswerLocked = true
        )

        viewModelScope.launch {
            delay(400) // Brief visual feedback
            recordAndNext(choiceId = choiceId, timeTaken = timeTakenSeconds)
        }
    }

    private fun recordAndNext(choiceId: String?, timeTaken: Double) {
        val data = questionsResponse ?: return
        val currentState = _uiState.value as? QuizUiState.Playing ?: return
        val currentQ = data.questions[currentState.currentQuestionIndex]

        recordedAnswers.add(
            UserAnswer(
                questionId = currentQ.id,
                selectedChoiceId = choiceId,
                timeTaken = (Math.round(timeTaken * 100.0) / 100.0)
            )
        )

        val nextIndex = currentState.currentQuestionIndex + 1
        if (nextIndex < data.questions.size) {
            startQuestion(nextIndex)
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        val data = questionsResponse ?: return

        val empty = recordedAnswers.count { it.selectedChoiceId == null }
        val answered = recordedAnswers.count { it.selectedChoiceId != null }
        val totalTime = recordedAnswers.sumOf { it.timeTaken }

        _uiState.value = QuizUiState.Finished(
            categoryData = data,
            answers = recordedAnswers.toList(),
            correctEstimated = 0,
            wrongEstimated = 0,
            emptyCount = empty,
            totalTimeTaken = (Math.round(totalTime * 10.0) / 10.0),
            isSubmitting = false,
            submitResponse = null,
            submitError = null
        )
    }

    fun submitQuiz(playerName: String) {
        val finishedState = _uiState.value as? QuizUiState.Finished ?: return
        val data = questionsResponse ?: return

        viewModelScope.launch {
            _uiState.value = finishedState.copy(isSubmitting = true, submitError = null)

            val request = QuizSubmitRequest(
                categorySlug = data.categorySlug,
                playerName = playerName,
                answers = recordedAnswers.map {
                    AnswerSubmissionDto(
                        questionId = it.questionId,
                        selectedChoiceId = it.selectedChoiceId,
                        timeTaken = it.timeTaken
                    )
                }
            )

            repository.submitQuiz(request)
                .onSuccess { response ->
                    _uiState.value = finishedState.copy(
                        isSubmitting = false,
                        submitResponse = response
                    )
                }
                .onFailure { error ->
                    _uiState.value = finishedState.copy(
                        isSubmitting = false,
                        submitError = error.localizedMessage ?: "Skor gönderilirken bir hata oluştu."
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
