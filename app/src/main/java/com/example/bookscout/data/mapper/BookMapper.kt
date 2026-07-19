package com.example.bookscout.data.mapper

import com.example.bookscout.data.api.model.OpenLibraryBookDto
import com.example.bookscout.domain.model.Book

fun OpenLibraryBookDto.toDomain(): Book {
    val constructedCoverUrl = if (coverId != null && coverId > 0) {
        "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
    } else {
        "" //Leaving this empty to flag that no cover is available
    }
    val formattedAuthor = when {
        authorNames.isEmpty() -> "Unknown Author"
        else -> authorNames.joinToString(separator = ", ")
    }

    return Book(
        id = key.removePrefix("/works/"), // Cleans up "/works/OL123W" to just "OL123W" for easier navigation
        title = title.ifBlank { "Untitled Book" },
        author = formattedAuthor,
        firstPublishYear = firstPublishYear?.toString() ?: "Year Unavailable",
        coverUrl = constructedCoverUrl,
        isbn = isbn.firstOrNull() ?: "N/A", // Takes the primary ISBN reference if it exists
        subjects = subject.take(5)
    )
}