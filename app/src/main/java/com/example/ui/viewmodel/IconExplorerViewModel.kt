package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ITunesAppResult
import com.example.data.model.SavedIcon
import com.example.data.repository.AppIconRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val results: List<ITunesAppResult>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

data class CuratedApp(
    val name: String,
    val trackId: Long,
    val artworkUrl512: String,
    val primaryGenreName: String,
    val artistName: String,
    val formattedPrice: String = "Free"
)

@OptIn(FlowPreview::class)
class IconExplorerViewModel(
    private val repository: AppIconRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isMacPlatform = MutableStateFlow(false)
    val isMacPlatform: StateFlow<Boolean> = _isMacPlatform.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    val savedIcons: StateFlow<List<SavedIcon>> = repository.savedIcons
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedApp = MutableStateFlow<ITunesAppResult?>(null)
    val selectedApp: StateFlow<ITunesAppResult?> = _selectedApp.asStateFlow()

    // Curator's Choice beautiful app selections
    val curatedApps = listOf(
        CuratedApp("Things 3", 904239854L, "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/44/14/d0/4414d0f6-932b-47e0-fbba-5ff49fd163ca/AppIcon-0-0-1x_U007emarketing-0-10-0-85-220.png/512x512bb.jpg", "Productivity", "Cultured Code", "$9.99"),
        CuratedApp("Bear Markdown Notes", 1091189122L, "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/a4/09/b3/a409b307-29cb-9df0-761e-fc360bc70ffc/AppIcon-0-0-1x_U007emarketing-0-7-0-85-220.png/512x512bb.jpg", "Productivity", "Shiny Frog Ltd.", "Free"),
        CuratedApp("Craft - Docs and Notes", 1487019481L, "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/bf/1a/0c/bf1a0cdf-7bb1-81d3-3bdf-6d04fbc5da47/AppIcon_Production-0-0-1x_U007emarketing-0-4-0-85-220.png/512x512bb.jpg", "Productivity", "Luki Labs", "Free"),
        CuratedApp("Flighty – Flight Tracker", 1350630047L, "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/05/cf/43/05cf4336-b636-df41-8625-ce6107f7b3df/AppIcon-1x_U007emarketing-0-7-0-0-85-220.png/512x512bb.jpg", "Travel", "Flighty LLC", "Free"),
        CuratedApp("Halide Mark II - Pro Camera", 1461900143L, "https://is1-ssl.mzstatic.com/image/thumb/Purple221/v4/e5/23/e8/e523e803-fa1c-c7ea-2a31-97b7ced12059/AppIcon-0-0-1x_U007emarketing-0-11-0-85-220.png/512x512bb.jpg", "Photo & Video", "Lux Camera", "Free"),
        CuratedApp("Procreate Pocket", 916856032L, "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/c3/00/ce/c300cee0-cf00-b8fb-df2a-57cae0b965bc/AppIcon-0-0-2x_U007emarketing-0-8-0-85-220.png/512x512bb.jpg", "Graphics & Design", "Savage Interactive", "$5.99")
    )

    init {
        // Trigger responsive debounced search on query changes
        viewModelScope.launch {
            combine(_searchQuery, _isMacPlatform) { query, isMac -> Pair(query, isMac) }
                .debounce(500)
                .collect { (query, isMac) ->
                    performSearch(query, isMac)
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onPlatformToggled(isMac: Boolean) {
        _isMacPlatform.value = isMac
    }

    fun selectApp(app: ITunesAppResult?) {
        _selectedApp.value = app
    }

    fun selectCuratedApp(curated: CuratedApp) {
        // Transform CuratedApp to ITunesAppResult to display in detail sheet
        val appResult = ITunesAppResult(
            trackId = curated.trackId,
            trackName = curated.name,
            artistName = curated.artistName,
            artworkUrl512 = curated.artworkUrl512,
            artworkUrl100 = curated.artworkUrl512, // fallback
            artworkUrl60 = curated.artworkUrl512,  // fallback
            price = if (curated.formattedPrice == "Free") 0.0 else 9.99,
            formattedPrice = curated.formattedPrice,
            primaryGenreName = curated.primaryGenreName,
            averageUserRating = 4.8,
            userRatingCount = 1200,
            trackViewUrl = "https://apps.apple.com/app/id${curated.trackId}",
            version = "1.0",
            releaseNotes = "Curated Outstanding Icon Design",
            screenshotUrls = emptyList()
        )
        _selectedApp.value = appResult
    }

    fun selectSavedIcon(saved: SavedIcon) {
        val appResult = ITunesAppResult(
            trackId = saved.id.toLongOrNull(),
            trackName = saved.name,
            artistName = saved.developer,
            artworkUrl512 = saved.artworkUrl512,
            artworkUrl100 = saved.artworkUrl100,
            artworkUrl60 = saved.artworkUrl100,
            price = 0.0,
            formattedPrice = saved.formattedPrice,
            primaryGenreName = saved.primaryGenreName,
            averageUserRating = saved.averageUserRating,
            userRatingCount = 200,
            trackViewUrl = saved.trackViewUrl,
            version = null,
            releaseNotes = null,
            screenshotUrls = emptyList()
        )
        _selectedApp.value = appResult
    }

    private suspend fun performSearch(query: String, isMac: Boolean) {
        if (query.trim().isEmpty()) {
            _searchUiState.value = SearchUiState.Idle
            return
        }

        _searchUiState.value = SearchUiState.Loading
        try {
            val results = repository.searchApps(query, isMac)
            _searchUiState.value = SearchUiState.Success(results)
        } catch (e: Exception) {
            _searchUiState.value = SearchUiState.Error(e.localizedMessage ?: "Network request failed.")
        }
    }

    fun toggleSaveApp(app: ITunesAppResult) {
        val trackIdStr = app.trackId?.toString() ?: return
        viewModelScope.launch {
            val isCurrentlySaved = savedIcons.value.any { it.id == trackIdStr }
            if (isCurrentlySaved) {
                repository.removeIcon(trackIdStr)
            } else {
                val saved = SavedIcon(
                    id = trackIdStr,
                    name = app.trackName ?: "Unknown",
                    developer = app.artistName ?: "Unknown",
                    artworkUrl512 = app.artworkUrl512 ?: "",
                    artworkUrl100 = app.artworkUrl100 ?: "",
                    formattedPrice = app.formattedPrice ?: "Free",
                    primaryGenreName = app.primaryGenreName ?: "Utilities",
                    averageUserRating = app.averageUserRating ?: 4.0,
                    trackViewUrl = app.trackViewUrl ?: ""
                )
                repository.saveIcon(saved)
            }
        }
    }

    class Factory(private val repository: AppIconRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IconExplorerViewModel::class.java)) {
                return IconExplorerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
