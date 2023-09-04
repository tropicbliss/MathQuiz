package net.tropicbliss.mathquiz.data

import net.tropicbliss.mathquiz.R

enum class QuizMode {
    Estimation,
    Precision;

    fun getTotalTimeInMinutes(): Int {
        return when (this) {
            Estimation -> 3
            Precision -> 1
        }
    }

    fun getStringResource(): Int {
        return when (this) {
            Estimation -> R.string.estimation
            Precision -> R.string.precision
        }
    }
}