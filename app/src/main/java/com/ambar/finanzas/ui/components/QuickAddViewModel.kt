package com.ambar.finanzas.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SaveMovementState(val busy: Boolean = false, val saved: Boolean = false, val error: String? = null)

class QuickAddViewModel(private val repository: FinanceRepository) : ViewModel() {
    private val _state = MutableStateFlow(SaveMovementState())
    val state = _state.asStateFlow()
    fun consume() { if (!_state.value.busy) _state.value = SaveMovementState() }
    fun save(amount: Long, description: String, expense: Boolean, date: LocalDate, pending: Boolean,
        categoryId: Long?, note: String, repeat: Boolean, subscription: Boolean, ruleId: Long?) {
        if (_state.value.busy || _state.value.saved) return
        _state.value = SaveMovementState(busy = true)
        viewModelScope.launch {
            try {
                repository.saveMovement(amount, description, expense, date, pending, categoryId, note, repeat, subscription, ruleId)
                _state.value = SaveMovementState(saved = true)
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _state.value = SaveMovementState(error = "No se pudo guardar. Tus datos anteriores siguen intactos. Inténtalo nuevamente.") }
        }
    }
    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = QuickAddViewModel(repository) as T
    }
}
