package net.tropicbliss.mathquiz.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.tropicbliss.mathquiz.data.Quiz
import net.tropicbliss.mathquiz.data.QuizMode
import net.tropicbliss.mathquiz.data.QuizzesRepository
import kotlin.math.abs
import kotlin.collections.average
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class QuizViewModel(private val quizzesRepository: QuizzesRepository) : ViewModel() {
    var quizMode = QuizMode.Estimation

    var userAnswer by mutableStateOf("")
        private set

    var currentProblem by mutableStateOf(generateRandomProblem())
        private set

    private var answeredProblems: MutableList<AnsweredProblem> = mutableListOf()

    private fun generateRandomProblem(): Problem {
        val maxOperand = when (quizMode) {
            QuizMode.Precision -> 10
            QuizMode.Estimation -> 1999
        }
        val operand1 = (1..maxOperand).random()
        val operand2 = (1..maxOperand).random()
        return Problem(operand1, operand2)
    }

    fun updateUserAnswer(answer: String) {
        userAnswer = answer
    }

    fun submit() {
        val iUserAnswer = userAnswer.toIntOrNull() ?: return
        val actualAnswer = currentProblem.operand1 * currentProblem.operand2
        var accuracyPercentage: Float? = null
        var variancePercentage: Int? = null
        var acceptableRange: String? = null
        val isCorrect = when (quizMode) {
            QuizMode.Precision -> actualAnswer == iUserAnswer
            QuizMode.Estimation -> {
                val tempVariancePercentage =
                    (actualAnswer - iUserAnswer).toFloat() / actualAnswer * 100
                variancePercentage = tempVariancePercentage.roundToInt()
                accuracyPercentage = 100 - abs(tempVariancePercentage)
                val min = ceil(0.8f * actualAnswer).toInt()
                val max = floor(1.2f * actualAnswer).toInt()
                acceptableRange = "$min - $max"
                (min..max).contains(iUserAnswer)
            }
        }
        answeredProblems.add(
            AnsweredProblem(
                problem = currentProblem,
                userAnswer = iUserAnswer,
                isCorrect = isCorrect,
                accuracyPercentage = accuracyPercentage,
                actualAnswer = actualAnswer,
                variancePercentage = variancePercentage,
                acceptableRange = acceptableRange
            )
        )
        updateUserAnswer("")
        currentProblem = generateRandomProblem()
    }

    suspend fun exportResults(): Results {
        val averageAccuracy: Int? = try {
            answeredProblems.mapNotNull { it.accuracyPercentage }.average().roundToInt()
        } catch (_: IllegalArgumentException) {
            null
        }
        val questionsPerMinute =
            (answeredProblems.count().toFloat() / quizMode.getTotalTimeInMinutes()).roundToInt()
        quizzesRepository.insertQuiz(
            Quiz(
                questionsPerMinute = questionsPerMinute,
                averageAccuracy = averageAccuracy,
                mode = quizMode.name,
                timeDelta = ((System.currentTimeMillis() - 1693248775000L) / 1000L).toInt()
            )
        )
        return Results(
            averageAccuracy = averageAccuracy,
            questionsPerMinute = questionsPerMinute,
            problems = answeredProblems.toList()
        )
    }
}

data class Problem(
    val operand1: Int,
    val operand2: Int
)

data class Results(
    val averageAccuracy: Int?,
    val questionsPerMinute: Int,
    val problems: List<AnsweredProblem>
)

data class AnsweredProblem(
    val problem: Problem,
    val userAnswer: Int,
    val actualAnswer: Int,
    val isCorrect: Boolean,
    val variancePercentage: Int?,
    val acceptableRange: String?,
    val accuracyPercentage: Float?
)