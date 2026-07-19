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
            val response = RetrofitClient.openLibraryApi.searchBooks(query = query, page = page)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val domainBooks = body.docs.map { it.toDomain() }
                    NetworkResult.Success(domainBooks)
                } else {
                    NetworkResult.Error(IllegalStateException("API returned an empty response body."))
                }
            } else {
                NetworkResult.Error(IOException("Server error occurred with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            NetworkResult.Error(e)
        }
    }

    override suspend fun getBookDetails(id: String): NetworkResult<Book> {
        return try {
            val response = RetrofitClient.openLibraryApi.getBookDetails(id)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Uses your existing OpenLibraryBookDto.toDomain() extension function!
                    NetworkResult.Success(body.toDomain())
                } else {
                    NetworkResult.Error(IllegalStateException("API returned an empty details payload."))
                }
            } else {
                NetworkResult.Error(IOException("Server error occurred with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            NetworkResult.Error(e)
        }
    }
}