package com.example.bookscout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.bookscout.data.datastore.UserPreferencesRepository
import com.example.bookscout.domain.model.AppSettings
import com.example.bookscout.ui.navigation.AppNavigation
import com.example.bookscout.ui.theme.BookScoutTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            val settingsState by preferencesRepository.appSettingsFlow
                .collectAsState(initial = AppSettings())

            BookScoutTheme(darkTheme = settingsState.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}