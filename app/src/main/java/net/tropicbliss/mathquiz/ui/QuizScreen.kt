package net.tropicbliss.mathquiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.tropicbliss.mathquiz.R

@Composable
fun QuizScreen(quizModel: QuizViewModel, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(
            dimensionResource(R.dimen.padding_medium)
        ).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${quizModel.currentProblem.operand1} × ${quizModel.currentProblem.operand2}",
            style = MaterialTheme.typography.displayLarge
        )
        Column {
            OutlinedTextField(value = quizModel.userAnswer, enabled = false, placeholder = {
                Text(stringResource(R.string.answer))
            }, onValueChange = {}, modifier = modifier.fillMaxWidth())
            NumPad(onNumPress = {
                quizModel.updateUserAnswer(quizModel.userAnswer + it)
            }, onBackspace = {
                quizModel.updateUserAnswer(quizModel.userAnswer.dropLast(1))
            }, onSubmit = {
                quizModel.submit()
            })
        }
    }
}

@Composable
fun NumPad(onNumPress: (String) -> Unit, onBackspace: () -> Unit, onSubmit: () -> Unit) {
    NumRow(
        texts = listOf("7", "8", "9"), weights = listOf(0.33f, 0.33f, 0.33f), callback = onNumPress
    )
    NumRow(
        texts = listOf("4", "5", "6"), weights = listOf(0.33f, 0.33f, 0.33f), callback = onNumPress
    )
    NumRow(
        texts = listOf("1", "2", "3"), weights = listOf(0.33f, 0.33f, 0.33f), callback = onNumPress
    )
    NumRow(texts = listOf("0", "←", "Submit"), weights = listOf(0.33f, 0.33f, 0.33f), callback = {
        when (it) {
            "Submit" -> onSubmit()
            "←" -> onBackspace()
            else -> onNumPress(it)
        }
    })
}

@Composable
fun NumRow(
    texts: List<String>, weights: List<Float>, callback: (text: String) -> Any
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (i in texts.indices) {
            NumButton(text = texts[i], callback = callback, modifier = Modifier.weight(weights[i]))
        }
    }
}

@Composable
fun NumButton(
    text: String, callback: (text: String) -> Any, modifier: Modifier = Modifier
) {
    Button(onClick = {
        callback(text)
    }, modifier = modifier.padding(4.dp)) {
        Text(text)
    }
}
