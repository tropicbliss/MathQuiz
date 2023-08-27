package net.tropicbliss.mathquiz.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.tropicbliss.mathquiz.data.QuizUiState
import net.tropicbliss.mathquiz.data.QuizzesRepository

class QuizViewModel(private val quizzesRepository: QuizzesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
}