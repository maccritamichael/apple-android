package com.example.data.repository

import com.example.data.api.ITunesApiService
import com.example.data.local.SavedIconDao
import com.example.data.model.ITunesAppResult
import com.example.data.model.SavedIcon
import kotlinx.coroutines.flow.Flow

class AppIconRepository(
    private val savedIconDao: SavedIconDao,
    private val apiService: ITunesApiService = ITunesApiService.instance
) {
    val savedIcons: Flow<List<SavedIcon>> = savedIconDao.getAllSavedIcons()

    suspend fun saveIcon(icon: SavedIcon) {
        savedIconDao.insertSavedIcon(icon)
    }

    suspend fun removeIcon(id: String) {
        savedIconDao.deleteSavedIconById(id)
    }

    fun isIconSaved(id: String): Flow<Boolean> {
        return savedIconDao.isIconSaved(id)
    }

    suspend fun searchApps(term: String, isMac: Boolean = false): List<ITunesAppResult> {
        if (term.trim().isEmpty()) return emptyList()
        val entity = if (isMac) "macSoftware" else "software"
        return try {
            val response = apiService.searchApps(term = term, entity = entity)
            response.results.filter { it.trackId != null && it.trackName != null && it.artworkUrl512 != null }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
