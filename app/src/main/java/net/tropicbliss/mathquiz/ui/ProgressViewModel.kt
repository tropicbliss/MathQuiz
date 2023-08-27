package net.tropicbliss.mathquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.tropicbliss.mathquiz.data.Quiz
import net.tropicbliss.mathquiz.data.QuizzesRepository

class ProgressViewModel(quizzesRepository: QuizzesRepository) : ViewModel() {
    val progressUiState: StateFlow<ProgressUiState> = quizzesRepository.getAllQuizzesStream().map {
        ProgressUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ProgressUiState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ProgressUiState(val quizList: List<Quiz> = listOf())