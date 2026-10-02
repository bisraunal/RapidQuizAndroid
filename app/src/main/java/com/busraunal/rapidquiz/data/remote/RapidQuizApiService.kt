package com.busraunal.rapidquiz.data.remote

import com.busraunal.rapidquiz.data.model.*
import retrofit2.http.*

interface RapidQuizApiService {

    @GET("categories/")
    suspend fun getCategories(): List<CategoryDto>

    @GET("categories/{slug}/questions/")
    suspend fun getCategoryQuestions(
        @Path("slug") slug: String
    ): CategoryQuestionsResponse

    @POST("quiz/submit/")
    suspend fun submitQuiz(
        @Body request: QuizSubmitRequest
    ): QuizSubmitResponse

    @GET("leaderboard/")
    suspend fun getCategoryLeaderboard(
        @Query("category") categorySlug: String,
        @Query("limit") limit: Int = 10
    ): LeaderboardResponse

    @GET("leaderboard/global/")
    suspend fun getGlobalLeaderboard(
        @Query("limit") limit: Int = 10
    ): LeaderboardResponse
}
