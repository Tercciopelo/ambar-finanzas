package com.ambar.finanzas.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.ambar.finanzas.data.repository.FinanceRepository

class SettingsViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    // TODO: implement settings state

    class Factory(private val repository: FinanceRepository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
