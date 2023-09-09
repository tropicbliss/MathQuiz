package net.tropicbliss.mathquiz.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import net.tropicbliss.mathquiz.MathQuizApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            QuizViewModel(mathQuizApplication().container.quizzesRepository)
        }
        initializer {
            ProgressViewModel(mathQuizApplication().container.quizzesRepository)
        }
    }
}

fun CreationExtras.mathQuizApplication(): MathQuizApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MathQuizApplication)