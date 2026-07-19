package com.example.bookscout.data.api.openlibrary

import com.example.bookscout.data.api.model.OpenLibrarySearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
interface OpenLibraryApi {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q")
        query: String,

        @Query("page")
        page: Int = 1,

        @Query("limit")
        limit: Int = 10,

        @Query("fields")
        fields: String = "key,title,author_name,first_publish_year,cover_i,isbn,subject"
    ): Response<OpenLibrarySearchResponse>
    @GET("works/{id}.json")
    suspend fun getBookDetails(
        @Path("id") id: String
    ): Response<com.example.bookscout.data.api.model.OpenLibraryBookDto>
}