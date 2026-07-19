package com.example.bookscout.ui.navigation

object Routes {
    const val SEARCH = "search_screen"
    const val SETTINGS = "settings_screen"

    // The details route includes a mandatory path argument for the unique book key string
    const val DETAILS = "details_screen/{bookId}"

    fun createDetailsRoute(bookId: String): String {
        return "details_screen/$bookId"
    }
}