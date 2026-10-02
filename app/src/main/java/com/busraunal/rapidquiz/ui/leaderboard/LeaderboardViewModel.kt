package com.busraunal.rapidquiz.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busraunal.rapidquiz.data.model.ScoreDto
import com.busraunal.rapidquiz.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardTab(
    val title: String,
    val slug: String // "global", "ascilik", "yazilim", "yapay-zeka", "bilgisayar-muhendisligi", "fizik", "futbol", "ulkeler"
)

sealed interface LeaderboardUiState {
    object Loading : LeaderboardUiState
    data class Success(
        val categoryTitle: String,
        val scores: List<ScoreDto>
    ) : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
}

class LeaderboardViewModel(
    private val repository: QuizRepository = QuizRepository()
) : ViewModel() {

    val tabs = listOf(
        LeaderboardTab("🌐 Genel", "global"),
        LeaderboardTab("🍳 Aşçılık", "ascilik"),
        LeaderboardTab("💻 Yazılım", "yazilim"),
        LeaderboardTab("🤖 Yapay Zeka", "yapay-zeka"),
        LeaderboardTab("⚙️ Bilg. Müh.", "bilgisayar-muhendisligi"),
        LeaderboardTab("⚛️ Fizik", "fizik"),
        LeaderboardTab("🏆 Futbol", "futbol"),
        LeaderboardTab("🌍 Ülkeler", "ulkeler")
    )

    private val _selectedTab = MutableStateFlow(tabs.first())
    val selectedTab: StateFlow<LeaderboardTab> = _selectedTab.asStateFlow()

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard(tabs.first().slug)
    }

    fun selectTab(tab: LeaderboardTab) {
        if (_selectedTab.value == tab) return
        _selectedTab.value = tab
        loadLeaderboard(tab.slug)
    }

    fun loadLeaderboard(slug: String) {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading
            val result = if (slug == "global") {
                repository.getGlobalLeaderboard()
            } else {
                repository.getCategoryLeaderboard(slug)
            }

            result.onSuccess { response ->
                _uiState.value = LeaderboardUiState.Success(
                    categoryTitle = response.category,
                    scores = response.leaderboard
                )
            }.onFailure { error ->
                _uiState.value = LeaderboardUiState.Error(
                    error.localizedMessage ?: "Lider tablosu yüklenemedi."
                )
            }
        }
    }
}
