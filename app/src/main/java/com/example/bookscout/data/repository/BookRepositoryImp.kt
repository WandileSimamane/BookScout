package com.example.bookscout.data.repository

import com.example.bookscout.data.api.openlibrary.RetrofitClient
import com.example.bookscout.data.mapper.toDomain
import com.example.bookscout.domain.model.Book
import com.example.bookscout.domain.repository.BookRepository
import com.example.bookscout.utils.NetworkResult
import kotlinx.coroutines.CancellationException
import java.io.IOException

object BookRepositoryImpl : BookRepository {

    override suspend fun searchBooks(query: String, page: Int): NetworkResult<List<Book>> {
        return try {
            val response = RetrofitClient.openLibraryApi.searchBooks(
                query = query,
                page = page
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Turn raw DTO documents into clean non-nullable domain models using your mapper
                    val domainBooks = body.docs.map { it.toDomain() }
                    NetworkResult.Success(domainBooks)
                } else {
                    NetworkResult.Error(IllegalStateException("API returned an empty response body."))
                }
            } else {
                NetworkResult.Error(IOException("Server error occurred with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Crucial: Coroutine cancellations must not be swallowed by generic catch blocks
            if (e is CancellationException) throw e

            // Catches connection timeouts (SocketTimeoutException) or no internet (UnknownHostException)
            NetworkResult.Error(e)
        }
    }
}