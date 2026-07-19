package com.example.bookscout.ui.screens.details

import com.example.bookscout.domain.model.Book

data class DetailsUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val errorMessage: String? = null
)