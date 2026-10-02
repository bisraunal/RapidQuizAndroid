package com.busraunal.rapidquiz.data.repository

import com.busraunal.rapidquiz.data.model.*
import com.busraunal.rapidquiz.data.remote.RapidQuizApiService
import com.busraunal.rapidquiz.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(
    private val api: RapidQuizApiService = RetrofitClient.apiService
) {

    suspend fun getCategories(): Result<List<CategoryDto>> = withContext(Dispatchers.IO) {
        try {
            val categories = api.getCategories()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryQuestions(slug: String): Result<CategoryQuestionsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategoryQuestions(slug)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(request: QuizSubmitRequest): Result<QuizSubmitResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.submitQuiz(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryLeaderboard(categorySlug: String): Result<LeaderboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategoryLeaderboard(categorySlug)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGlobalLeaderboard(): Result<LeaderboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGlobalLeaderboard()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
