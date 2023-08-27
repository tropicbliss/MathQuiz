package net.tropicbliss.mathquiz.data

import android.content.Context

interface AppContainer {
    val quizzesRepository: QuizzesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val quizzesRepository: QuizzesRepository by lazy {
        OfflineQuizzesRepository(ProgressDatabase.getDatabase(context).quizDao())
    }
}