package com.aowen.datastoredemo

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aowen.datastoredemo.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScreenUIState(
    val usernameField: String,
    val selectedColor: Color
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val userPreferences = userPreferencesRepository.userPreferencesFlow

    private val _uiState: MutableStateFlow<ScreenUIState> = MutableStateFlow(
        ScreenUIState(
            usernameField = "",
            selectedColor = Color.Red
        )
    )
    val uiState = _uiState

    init {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.fetchInitialUserPreferences()
            _uiState.update {
                it.copy(
                    usernameField = prefs.userName,
                    selectedColor = Color(prefs.backgroundColor)
                )
            }
        }
    }

    fun updateUserName(newName: String) {
        _uiState.update { it.copy(usernameField = newName) }
    }

    fun updateBackgroundColor(newColor: Color) {
        _uiState.update { it.copy(selectedColor = newColor) }
    }

    fun updatePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.updateUserName(uiState.value.usernameField)
            userPreferencesRepository.updateBackgroundColor(uiState.value.selectedColor.toArgb())
        }
    }



}