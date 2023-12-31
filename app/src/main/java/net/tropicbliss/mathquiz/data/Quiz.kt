package net.tropicbliss.mathquiz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val averageAccuracy: Int?,
    val questionsPerMinute: Int,
    val mode: String,
    val timeDelta: Int
)