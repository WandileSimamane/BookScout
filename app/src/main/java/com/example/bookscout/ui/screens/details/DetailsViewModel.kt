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
        val bookId: String? = savedStateHandle["bookId"]
        if (bookId != null) {
            loadBookDetails(bookId)
        } else {
            _uiState.update { it.copy(errorMessage = "Missing target book reference.") }
        }
    }

    private fun loadBookDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = bookRepository.getBookDetails(id)) {
                is NetworkResult.Success<*> -> {
                    _uiState.update { it.copy(book = result.data as? com.example.bookscout.domain.model.Book, isLoading = false) }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.localizedMessage ?: "Failed to retrieve book details.",
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}