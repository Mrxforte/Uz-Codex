package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE articleId = :articleId)")
    fun isArticleBookmarked(articleId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE articleId = :articleId")
    suspend fun deleteBookmark(articleId: String)

    // Notes
    @Query("SELECT * FROM user_notes WHERE articleId = :articleId ORDER BY createdAt DESC")
    fun getNotesForArticle(articleId: String): Flow<List<UserNoteEntity>>

    @Query("SELECT * FROM user_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<UserNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UserNoteEntity)

    @Delete
    suspend fun deleteNote(note: UserNoteEntity)

    // Saved Cases
    @Query("SELECT * FROM saved_case_analyses ORDER BY savedAt DESC")
    fun getAllSavedCases(): Flow<List<SavedCaseAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCase(savedCase: SavedCaseAnalysisEntity)

    @Query("DELETE FROM saved_case_analyses WHERE id = :id")
    suspend fun deleteSavedCase(id: String)

    // User Profile & Gamification
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :addedXp WHERE id = 1")
    suspend fun addXp(addedXp: Int)

    // Lessons Progress
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markLessonCompleted(progress: LessonProgressEntity)

    // --- RAG Test Generator Persistence ---

    @Query("SELECT * FROM generated_test_sets ORDER BY createdAt DESC")
    fun getAllGeneratedTestSets(): Flow<List<GeneratedTestSetEntity>>

    @Query("SELECT * FROM generated_test_sets WHERE id = :id")
    suspend fun getGeneratedTestSetById(id: String): GeneratedTestSetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedTestSet(testSet: GeneratedTestSetEntity)

    @Query("DELETE FROM generated_test_sets WHERE id = :id")
    suspend fun deleteGeneratedTestSet(id: String)

    @Query("SELECT * FROM generated_questions WHERE testSetId = :testSetId")
    suspend fun getQuestionsForTestSet(testSetId: String): List<GeneratedQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedQuestions(questions: List<GeneratedQuestionEntity>)

    @Query("DELETE FROM generated_questions WHERE testSetId = :testSetId")
    suspend fun deleteQuestionsForTestSet(testSetId: String)

    // Attempts History
    @Query("SELECT * FROM test_attempt_history ORDER BY timestamp DESC")
    fun getAllTestAttempts(): Flow<List<TestAttemptHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestAttempt(attempt: TestAttemptHistoryEntity)

    // Duplicate Detection Fingerprints
    @Query("SELECT fingerprint FROM generated_fingerprints")
    suspend fun getAllGeneratedFingerprints(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFingerprints(fingerprints: List<GeneratedFingerprintEntity>)
}
