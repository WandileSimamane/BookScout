package com.example.bookscout.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookscout.data.repository.BookRepositoryImpl
import com.example.bookscout.domain.repository.BookRepository
import com.example.bookscout.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val bookRepository: BookRepository = BookRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun searchBooks() {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = bookRepository.searchBooks(currentQuery, page = 1)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(books = result.data, isLoading = false)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.localizedMessage ?: "An unexpected error occurred",
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun updateLayoutPreference(isGridView: Boolean) {
        _uiState.update { it.copy(isGridView = isGridView) }
    }
}