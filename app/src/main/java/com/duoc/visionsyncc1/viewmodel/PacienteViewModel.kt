package com.duoc.visionsyncc1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.repository.ExamenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PacienteViewModel(
    private val repository: ExamenRepository
) : ViewModel(){
    val examenesAutorizados: StateFlow<List<ExamenEntity>> =
        repository.observarAutorizados()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}

class PacienteViewModelFactory(private val repo: ExamenRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PacienteViewModel(repo) as T
}