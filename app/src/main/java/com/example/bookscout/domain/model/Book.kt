package com.example.bookscout.domain.model

data class Book(
    val id: String = "",
    val title: String = "Untitled Book",
    val author: String = "Unknown Author",
    val firstPublishYear: String = "Year Unavailable",
    val coverUrl: String = "",
    val isbn: String = "N/A",
    val subjects: List<String> = emptyList()
) {
    val hasCover: Boolean
        get() = coverUrl.isNotEmpty()
}