package com.busraunal.rapidquiz.data.model

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("color_theme") val colorTheme: String,
    @SerializedName("music_url") val musicUrl: String?,
    @SerializedName("music_title") val musicTitle: String?,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("question_count") val questionCount: Int
)

data class CategoryQuestionsResponse(
    @SerializedName("category") val category: String,
    @SerializedName("category_slug") val categorySlug: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("color_theme") val colorTheme: String,
    @SerializedName("music_url") val musicUrl: String?,
    @SerializedName("music_title") val musicTitle: String?,
    @SerializedName("time_per_question") val timePerQuestion: Int = 5,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("questions") val questions: List<QuestionDto>
)

data class QuestionDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("code_snippet") val codeSnippet: String?,
    @SerializedName("points") val points: Int = 10,
    @SerializedName("order") val order: Int = 0,
    @SerializedName("choices") val choices: List<ChoiceDto>
)

data class ChoiceDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String
)

data class QuizSubmitRequest(
    @SerializedName("category_slug") val categorySlug: String,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("answers") val answers: List<AnswerSubmissionDto>
)

data class AnswerSubmissionDto(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("selected_choice_id") val selectedChoiceId: String?,
    @SerializedName("time_taken") val timeTaken: Double
)

data class QuizSubmitResponse(
    @SerializedName("score_id") val scoreId: String,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("category_slug") val categorySlug: String,
    @SerializedName("total_score") val totalScore: Int,
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("wrong_count") val wrongCount: Int,
    @SerializedName("empty_count") val emptyCount: Int,
    @SerializedName("total_time_taken") val totalTimeTaken: Double,
    @SerializedName("rank") val rank: Int,
    @SerializedName("results_breakdown") val resultsBreakdown: List<QuestionResultDto>? = null,
    @SerializedName("top_10") val top10: List<ScoreDto>
)

data class QuestionResultDto(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("code_snippet") val codeSnippet: String?,
    @SerializedName("selected_choice_id") val selectedChoiceId: String?,
    @SerializedName("selected_choice_text") val selectedChoiceText: String?,
    @SerializedName("correct_choice_id") val correctChoiceId: String?,
    @SerializedName("correct_choice_text") val correctChoiceText: String?,
    @SerializedName("is_correct") val isCorrect: Boolean,
    @SerializedName("time_taken") val timeTaken: Double,
    @SerializedName("earned_points") val earnedPoints: Int
)

data class LeaderboardResponse(
    @SerializedName("category") val category: String,
    @SerializedName("category_slug") val categorySlug: String,
    @SerializedName("leaderboard") val leaderboard: List<ScoreDto>
)

data class ScoreDto(
    @SerializedName("id") val id: String,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("total_score") val totalScore: Int,
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("wrong_count") val wrongCount: Int,
    @SerializedName("empty_count") val emptyCount: Int,
    @SerializedName("total_time_taken") val totalTimeTaken: Double,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("category_slug") val categorySlug: String?,
    @SerializedName("created_at") val createdAt: String
)
