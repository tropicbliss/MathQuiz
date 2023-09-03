package net.tropicbliss.mathquiz.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val progressUiState by viewModel.progressUiState.collectAsState()
}