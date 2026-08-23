package com.aicane.app.presentation.app

import androidx.lifecycle.ViewModel
import com.aicane.app.data.remote.auth.AuthEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authEventBus: AuthEventBus,
) : ViewModel() {
    val sessionExpiredFlow: SharedFlow<Unit> = authEventBus.sessionExpired
}
