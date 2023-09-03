package net.tropicbliss.mathquiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.tropicbliss.mathquiz.R

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel(factory = AppViewModelProvider.Factory), modifier: Modifier = Modifier) {
    val progressUiState by viewModel.progressUiState.collectAsState()

    LazyColumn(modifier = modifier) {
        items(progressUiState.quizList) { quiz ->
            Card(
                modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                    modifier = Modifier.padding(
                        dimensionResource(R.dimen.padding_medium)
                    )
                ) {
                    Text(
                        text = quiz.mode,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.questions_per_minute, quiz.questionsPerMinute),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (quiz.averageAccuracy != null) {
                            Text(
                                text = stringResource(R.string.average_accuracy, quiz.averageAccuracy),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}