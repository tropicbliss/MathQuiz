package net.tropicbliss.mathquiz

import android.app.Application
import net.tropicbliss.mathquiz.data.AppContainer
import net.tropicbliss.mathquiz.data.AppDataContainer

class MathQuizApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}