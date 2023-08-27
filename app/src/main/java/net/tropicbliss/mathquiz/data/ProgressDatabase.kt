package net.tropicbliss.mathquiz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Quiz::class], version = 1, exportSchema = false)
abstract class ProgressDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var Instance: ProgressDatabase? = null

        fun getDatabase(context: Context): ProgressDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ProgressDatabase::class.java, "progress_database")
                    .fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}