package com.example.bookscout.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookscout.data.datastore.UserPreferencesRepository
import com.example.bookscout.ui.screens.search.SearchScreen
import com.example.bookscout.ui.screens.search.SearchViewModel
import com.example.bookscout.ui.screens.details.DetailsScreen
import com.example.bookscout.ui.screens.details.DetailsViewModel
import com.example.bookscout.ui.screens.settings.SettingsScreen
import com.example.bookscout.ui.screens.settings.SettingsViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                viewModel = searchViewModel,
                onBookClick = { bookId ->
                    navController.navigate(Routes.createDetailsRoute(bookId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val detailsViewModel: DetailsViewModel = viewModel()
            DetailsScreen(
                viewModel = detailsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        SettingsViewModel(UserPreferencesRepository(context))
                    }
                }
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}