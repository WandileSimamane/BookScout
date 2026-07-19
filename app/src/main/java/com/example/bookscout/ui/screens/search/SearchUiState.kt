package com.example.bookscout.ui.screens.search

import com.example.bookscout.domain.model.Book

data class SearchUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGridView: Boolean = false,
    val currentPage: Int = 1,
    val endOfPaginationReached: Boolean = false
)