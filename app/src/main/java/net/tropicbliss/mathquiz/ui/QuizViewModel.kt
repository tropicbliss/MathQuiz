package net.tropicbliss.mathquiz.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.tropicbliss.mathquiz.data.Quiz
import net.tropicbliss.mathquiz.data.QuizMode
import net.tropicbliss.mathquiz.data.QuizzesRepository
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

const val MIN_ACCURACY = 0.8f

class QuizViewModel(private val quizzesRepository: QuizzesRepository) : ViewModel() {
    var quizMode = QuizMode.Estimation
        private set

    var userAnswer by mutableStateOf("")
        private set

    var currentProblem by mutableStateOf(generateRandomProblem())
        private set

    private var answeredProblems: MutableList<AnsweredProblem> = mutableListOf()

    private fun generateRandomProblem(): Problem {
        val operand1 = generateRandomOperand(null)
        val operand2 = generateRandomOperand(operand1)
        return Problem(operand1, operand2)
    }

    private fun generateRandomOperand(firstValueHint: Int?): Int {
        return when (quizMode) {
            QuizMode.Precision -> (1..10).random()
            QuizMode.Estimation -> {
                val isGenerateFirstDigit = if (firstValueHint == null) true else {
                    !(1..10).contains(firstValueHint)
                }
                val randomDigit = if (isGenerateFirstDigit) {
                    Digit.values().toList()
                } else {
                    listOf(Digit.Two, Digit.Three, Digit.Four)
                }.random()
                val range = when (randomDigit) {
                    Digit.One -> (1..9)
                    Digit.Two -> (10..99)
                    Digit.Three -> (100..999)
                    Digit.Four -> (1000..1999)
                }
                range.random()
            }
        }
    }

    fun setQuizMode(quizMode: QuizMode) {
        this.quizMode = quizMode
        currentProblem = generateRandomProblem()
    }

    fun updateUserAnswer(answer: String) {
        userAnswer = answer
    }

    fun clear() {
        userAnswer = ""
        answeredProblems.clear()
    }

    fun submit() {
        val iUserAnswer = userAnswer.toIntOrNull() ?: return
        val actualAnswer = currentProblem.operand1 * currentProblem.operand2
        var variancePercentage: Int? = null
        var acceptableRange: String? = null
        val isCorrect = when (quizMode) {
            QuizMode.Precision -> actualAnswer == iUserAnswer
            QuizMode.Estimation -> {
                val tempVariancePercentage =
                    (actualAnswer - iUserAnswer).toFloat() / actualAnswer * 100
                variancePercentage = tempVariancePercentage.roundToInt()
                val min = ceil(MIN_ACCURACY * actualAnswer).toInt()
                val max = floor((2f - MIN_ACCURACY) * actualAnswer).toInt()
                acceptableRange = "$min - $max"
                (min..max).contains(iUserAnswer)
            }
        }
        answeredProblems.add(
            AnsweredProblem(
                problem = currentProblem,
                userAnswer = iUserAnswer,
                isCorrect = isCorrect,
                actualAnswer = actualAnswer,
                variancePercentage = variancePercentage,
                acceptableRange = acceptableRange
            )
        )
        updateUserAnswer("")
        currentProblem = generateRandomProblem()
    }

    suspend fun exportResults(): Results {
        val averageAccuracy: Int? = if (answeredProblems.isEmpty()) {
            null
        } else {
            (answeredProblems.count { it.isCorrect }
                .toFloat() / answeredProblems.count()).roundToInt() * 100
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
    val operand1: Int, val operand2: Int
)

data class Results(
    val averageAccuracy: Int?, val questionsPerMinute: Int, val problems: List<AnsweredProblem>
)

data class AnsweredProblem(
    val problem: Problem,
    val userAnswer: Int,
    val actualAnswer: Int,
    val isCorrect: Boolean,
    val variancePercentage: Int?,
    val acceptableRange: String?
)

private enum class Digit {
    One,
    Two,
    Three,
    Four
}