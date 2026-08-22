package com.aicane.app.navigation

import com.aicane.app.domain.model.NavigationInstruction
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationStateHolder @Inject constructor() {
    val currentInstruction: MutableStateFlow<NavigationInstruction?> = MutableStateFlow(null)

    fun reset() {
        currentInstruction.value = null
    }
}
