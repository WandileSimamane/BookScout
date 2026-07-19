package com.example.bookscout.ui.screens.details

import androidx.lifecycle.SavedStateHandle
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

class DetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository = BookRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("bookId")?.let { bookId ->
            loadBookDetails(bookId)
        }
    }

    private fun loadBookDetails(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = bookRepository.searchBooks(bookId, page = 1)) {
                is NetworkResult.Success -> {
                    val matchedBook = result.data.firstOrNull { it.id == bookId }
                    if (matchedBook != null) {
                        _uiState.update { it.copy(book = matchedBook, isLoading = false) }
                    } else {
                        _uiState.update {
                            it.copy(errorMessage = "Book details not found.", isLoading = false)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.localizedMessage ?: "Failed to load details",
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
}