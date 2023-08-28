package net.tropicbliss.mathquiz.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.tropicbliss.mathquiz.MathQuizScreen
import net.tropicbliss.mathquiz.R
import net.tropicbliss.mathquiz.data.QuizMode

@Composable
fun StartScreen(onNavigate: (QuizMode) -> Unit) {
    Column {
        QuizCard(
            title = stringResource(R.string.estimation),
            description = stringResource(R.string.estimation_description),
            maxTime = QuizMode.Estimation.getTotalTimeInMinutes(),
            onClick = {
                onNavigate(QuizMode.Estimation)
            }
        )
        QuizCard(
            title = stringResource(R.string.precise),
            description = stringResource(R.string.precision_description),
            maxTime = QuizMode.Precision.getTotalTimeInMinutes(),
            onClick = {
                onNavigate(QuizMode.Precision)
            }
        )
    }
}

@Composable
fun QuizCard(title: String, description: String, maxTime: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium))
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(text = title, style = MaterialTheme.typography.displayMedium)
            Text(text = description, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${stringResource(R.string.total_time)}: ${
                    pluralStringResource(R.plurals.minute, maxTime, maxTime)
                }", style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    StartScreen(onNavigate = {})
}