package com.busraunal.rapidquiz.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busraunal.rapidquiz.data.model.CategoryDto
import com.busraunal.rapidquiz.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CategoryUiState {
    object Loading : CategoryUiState
    data class Success(val categories: List<CategoryDto>) : CategoryUiState
    data class Error(val message: String) : CategoryUiState
}

class CategoryViewModel(
    private val repository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            repository.getCategories()
                .onSuccess { list ->
                    _uiState.value = CategoryUiState.Success(list)
                }
                .onFailure { error ->
                    _uiState.value = CategoryUiState.Error(
                        error.localizedMessage ?: "Kategoriler yüklenirken bir hata oluştu."
                    )
                }
        }
    }
}
