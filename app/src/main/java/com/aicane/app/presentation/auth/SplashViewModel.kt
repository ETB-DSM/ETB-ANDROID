package com.aicane.app.presentation.auth

import androidx.lifecycle.ViewModel
import com.aicane.app.data.local.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    val hasSession: Boolean get() = tokenStorage.accessToken != null
}
