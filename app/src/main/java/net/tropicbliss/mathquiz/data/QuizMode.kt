package net.tropicbliss.mathquiz.data

enum class QuizMode {
    Estimation,
    Precision;

    fun getTotalTimeInMinutes(): Int {
        return when (this) {
            Estimation -> 3
            Precision -> 1
        }
    }
}