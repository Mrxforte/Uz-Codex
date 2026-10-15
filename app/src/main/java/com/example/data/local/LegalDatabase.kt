package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        UserNoteEntity::class,
        SavedCaseAnalysisEntity::class,
        UserProfileEntity::class,
        LessonProgressEntity::class,
        GeneratedTestSetEntity::class,
        GeneratedQuestionEntity::class,
        TestAttemptHistoryEntity::class,
        GeneratedFingerprintEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LegalDatabase : RoomDatabase() {
    abstract fun legalDao(): LegalDao

    companion object {
        @Volatile
        private var INSTANCE: LegalDatabase? = null

        fun getDatabase(context: Context): LegalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LegalDatabase::class.java,
                    "uzbekistan_legal_ecosystem.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
