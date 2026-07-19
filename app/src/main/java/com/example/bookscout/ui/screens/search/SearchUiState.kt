package com.example.bookscout.ui.screens.search

import com.example.bookscout.domain.model.Book

data class SearchUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGridView: Boolean = false
)