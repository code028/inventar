package com.code028.inventoryapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.code028.inventoryapp.data.model.User
import com.code028.inventoryapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun currentUser() = authRepository.currentUser()
    fun logout() = authRepository.logout()

    // Unusable function
    fun updateUserProfileImage(uri: Uri, onResult: (Boolean, String?) -> Unit) {
        val uid = authRepository.currentUser()?.uid ?: return
        authRepository.uploadUserProfileImage(uid, uri, onResult)
    }


    fun getUserData(onResult: (User?) -> Unit) {
        val uid = authRepository.currentUser()?.uid ?: return onResult(null)
        authRepository.getUserData(uid, onResult)
    }

    fun updateProfileImageUrl(imageUrl: String, onResult: (Boolean, String?) -> Unit) {
        val uid = authRepository.currentUser()?.uid ?: return
        authRepository.updateUserProfileImageUrl(uid, imageUrl, onResult)
    }

    fun getUserItemCount(onResult: (Int) -> Unit) {
        val uid = currentUser()?.uid ?: return onResult(0)
        print("there is not items")
        authRepository.getUserItems(uid) { items ->
            onResult(items.size)
        }
    }
}
