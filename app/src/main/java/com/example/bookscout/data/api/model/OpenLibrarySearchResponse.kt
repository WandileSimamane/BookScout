package com.example.bookscout.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibrarySearchResponse(
    @SerialName("numFound")
    val numberFound: Int = 0,
    val docs: List<OpenLibraryBookDto> = emptyList()
)

@Serializable
data class OpenLibraryBookDto(
    val key: String = "",
    val title: String = "Untitled",

    @SerialName("author_name")
    val authorNames: List<String> = emptyList(),

    @SerialName("first_publish_year")
    val firstPublishYear: Int? = null,

    @SerialName("cover_i")
    val coverId: Int? = null,

    val isbn: List<String> = emptyList(),

    val subject: List<String> = emptyList()
)