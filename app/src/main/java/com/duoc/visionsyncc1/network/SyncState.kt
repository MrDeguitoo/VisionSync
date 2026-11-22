package com.duoc.visionsyncc1.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Estados de sincronización visibles en la UI
 */
sealed interface SyncState {
    object Offline : SyncState
    data class Pendiente(val count: Int) : SyncState
    object Sincronizado : SyncState
    data class Error(val mensaje: String) : SyncState
}

/**
 * Manager centralizado del estado de sincronización.
 * Se observa en la UI para mostrar banner "Modo offline", "Sincronización pendiente (N)", etc.
 */
class SyncStateManager {
    private val _state = MutableStateFlow<SyncState>(SyncState.Offline)
    val state: StateFlow<SyncState> = _state

    private var pendingCount = 0
    private var isOnline = false

    fun setOnline(online: Boolean) {
        isOnline = online
        if (online) {
            if (pendingCount > 0) {
                _state.value = SyncState.Pendiente(pendingCount)
            } else {
                _state.value = SyncState.Sincronizado
            }
        } else {
            _state.value = SyncState.Offline
        }
    }

    fun setPendingCount(count: Int) {
        pendingCount = count
        if (isOnline) {
            if (count > 0) {
                _state.value = SyncState.Pendiente(count)
            } else {
                _state.value = SyncState.Sincronizado
            }
        }
    }

    fun setError(mensaje: String) {
        _state.value = SyncState.Error(mensaje)
    }
}