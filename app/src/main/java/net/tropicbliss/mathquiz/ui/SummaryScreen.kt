package net.tropicbliss.mathquiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.tropicbliss.mathquiz.EmptyList
import net.tropicbliss.mathquiz.R

@Composable
fun SummaryScreen(
    results: Results, isDialogOpen: Boolean, onCloseInfo: () -> Unit, modifier: Modifier = Modifier
) {
    if (results.problems.isEmpty()) {
        EmptyList()
    } else {
        LazyColumn(modifier = modifier) {
            items(results.problems) { problem ->
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.padding_medium)
                        )
                    ) {
                        Text(
                            text = "${problem.problem.operand1} × ${problem.problem.operand2}",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.actual_answer, problem.actualAnswer),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.user_answer, problem.userAnswer),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (problem.isCorrect) Color.Green else Color.Red
                            )
                            if (problem.acceptableRange != null) {
                                Text(
                                    text = stringResource(
                                        R.string.acceptable_range, problem.acceptableRange
                                    ), style = MaterialTheme.typography.titleMedium
                                )
                            }
                            if (problem.variancePercentage != null) {
                                Text(
                                    text = stringResource(
                                        R.string.variance_percentage, problem.variancePercentage
                                    ), style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (isDialogOpen) {
        AlertDialog(onDismissRequest = {}, confirmButton = {
            TextButton(onClick = onCloseInfo) {
                Text(text = stringResource(R.string.dismiss))
            }
        }, title = {
            Text(text = stringResource(R.string.more_info))
        }, text = {
            Column {
                Text(
                    text = stringResource(
                        R.string.questions_per_minute, results.questionsPerMinute
                    )
                )
                if (results.averageAccuracy != null) {
                    Text(text = stringResource(R.string.average_accuracy, results.averageAccuracy))
                }
            }
        }, modifier = modifier)
    }
}