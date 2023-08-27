package net.tropicbliss.mathquiz.data

import kotlinx.coroutines.flow.Flow

interface QuizzesRepository {
    fun getAllQuizzesStream(): Flow<List<Quiz>>

    suspend fun insertQuiz(quiz: Quiz)
}