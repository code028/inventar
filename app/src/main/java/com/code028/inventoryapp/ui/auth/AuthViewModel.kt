package com.code028.inventoryapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code028.inventoryapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
    val authResult: LiveData<Pair<Boolean, String?>> = _authResult

    fun login(email: String, password: String) {
        authRepository.login(email, password) { success, msg ->
            _authResult.postValue(success to msg)
        }
    }

    fun register(email: String, password: String, name: String) {
        authRepository.register(email, password, name) { success, msg ->
            _authResult.postValue(success to msg)
        }
    }

    fun logout() = authRepository.logout()
    fun currentUser() = authRepository.currentUser()
}
