package com.giruai.climatest.presentation.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giruai.climatest.data.local.preferences.RecentSearchesManager
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.usecase.SearchCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCities: SearchCitiesUseCase,
    private val recentSearchesManager: RecentSearchesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<City>>(emptyList())
    val recentSearches: StateFlow<List<City>> = _recentSearches.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadRecentSearches()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery

        // Cancel previous search job
        searchJob?.cancel()

        if (newQuery.isEmpty()) {
            _uiState.value = SearchUiState.Empty
            return
        }

        if (newQuery.length < 2) {
            _uiState.value = SearchUiState.Empty
            return
        }

        // Debounce: wait 300ms before searching
        searchJob = viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            delay(DEBOUNCE_MS)

            try {
                val result = searchCities(newQuery)
                result.onSuccess { cities ->
                    _uiState.value = if (cities.isEmpty()) {
                        SearchUiState.NoResults
                    } else {
                        SearchUiState.Results(cities)
                    }
                }.onFailure { error ->
                    Timber.e(error, "Search failed for query: $newQuery")
                    _uiState.value = SearchUiState.Error(
                        error.message ?: "Search failed"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during search")
                _uiState.value = SearchUiState.Error(
                    "An unexpected error occurred"
                )
            }
        }
    }

    fun onCitySelected(city: City) {
        Timber.d("City selected: ${city.name}")
        recentSearchesManager.addRecentSearch(city)
        loadRecentSearches()
    }

    fun clearQuery() {
        _query.value = ""
        _uiState.value = SearchUiState.Empty
    }

    private fun loadRecentSearches() {
        _recentSearches.value = recentSearchesManager.getRecentSearches()
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
