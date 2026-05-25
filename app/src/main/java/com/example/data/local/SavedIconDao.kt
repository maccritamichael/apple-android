package com.example.data.local

import androidx.room.*
import com.example.data.model.SavedIcon
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedIconDao {
    @Query("SELECT * FROM saved_icons ORDER BY savedAt DESC")
    fun getAllSavedIcons(): Flow<List<SavedIcon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedIcon(icon: SavedIcon)

    @Query("DELETE FROM saved_icons WHERE id = :id")
    suspend fun deleteSavedIconById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_icons WHERE id = :id LIMIT 1)")
    fun isIconSaved(id: String): Flow<Boolean>
}
