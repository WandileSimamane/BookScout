package com.example.bookscout.domain.repository

import com.example.bookscout.domain.model.Book
import com.example.bookscout.utils.NetworkResult

interface BookRepository {
    suspend fun searchBooks(query: String, page: Int): NetworkResult<List<Book>>
    suspend fun getBookDetails(id: String): NetworkResult<Book>
}