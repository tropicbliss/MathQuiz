package net.tropicbliss.mathquiz.data

import kotlinx.coroutines.flow.Flow

class OfflineQuizzesRepository(private val quizDao: QuizDao) : QuizzesRepository {
    override fun getAllQuizzesStream(): Flow<List<Quiz>> = quizDao.getAllQuizzes()

    override suspend fun insertQuiz(quiz: Quiz) = quizDao.insert(quiz)
}