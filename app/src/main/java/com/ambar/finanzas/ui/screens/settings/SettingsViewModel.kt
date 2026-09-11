package com.ambar.finanzas.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FinanceRepository) : ViewModel() {
    val hideAmounts = repository.observeSetting("hide_amounts").map { it == "true" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val theme = repository.observeSetting("theme").map { it ?: "system" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "system")

    fun setHideAmounts(value: Boolean) = viewModelScope.launch {
        repository.setSetting("hide_amounts", value.toString())
    }

    fun setTheme(value: String) = viewModelScope.launch {
        repository.setSetting("theme", value)
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(repository) as T
    }
}
