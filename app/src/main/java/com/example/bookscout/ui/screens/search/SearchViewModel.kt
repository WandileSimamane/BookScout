package com.example.bookscout.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookscout.data.repository.BookRepositoryImpl
import com.example.bookscout.domain.repository.BookRepository
import com.example.bookscout.utils.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository = BookRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
    }

    init {
        val restoredQuery: String? = savedStateHandle[KEY_SEARCH_QUERY]
        if (!restoredQuery.isNullOrBlank()) {
            _uiState.update { it.copy(query = restoredQuery) }
            searchBooks(isInitialSearch = true)
        }
    }

    fun onQueryChanged(newQuery: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = newQuery
        _uiState.update { it.copy(query = newQuery) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (newQuery.trim().isNotBlank()) {
                searchBooks(isInitialSearch = true)
            }
        }
    }

    fun searchBooks(isInitialSearch: Boolean = true) {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isBlank()) return

        if (!isInitialSearch && (_uiState.value.isPaginationLoading || _uiState.value.endOfPaginationReached)) {
            return
        }

        val pageToLoad = if (isInitialSearch) 1 else _uiState.value.currentPage + 1

        viewModelScope.launch {
            if (isInitialSearch) {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null,
                        books = emptyList(),
                        currentPage = 1,
                        endOfPaginationReached = false
                    )
                }
            } else {
                _uiState.update { it.copy(isPaginationLoading = true) }
            }

            when (val result = bookRepository.searchBooks(currentQuery, page = pageToLoad)) {
                is NetworkResult.Success -> {
                    val newBooks = result.data
                    _uiState.update { current ->
                        current.copy(
                            books = current.books + newBooks,
                            isLoading = false,
                            isPaginationLoading = false,
                            currentPage = pageToLoad,
                            endOfPaginationReached = newBooks.isEmpty()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = if (isInitialSearch) {
                                result.exception.localizedMessage ?: "An unexpected error occurred"
                            } else null,
                            isLoading = false,
                            isPaginationLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateLayoutPreference(isGridView: Boolean) {
        _uiState.update { it.copy(isGridView = isGridView) }
    }
}