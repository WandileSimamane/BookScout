package com.example.bookscout.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookscout.data.datastore.UserPreferencesRepository
import com.example.bookscout.domain.model.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val settingsState: StateFlow<AppSettings> = preferencesRepository.appSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.toggleDarkMode(enabled)
        }
    }

    fun toggleLayoutView(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.toggleLayoutView(enabled)
        }
    }
}