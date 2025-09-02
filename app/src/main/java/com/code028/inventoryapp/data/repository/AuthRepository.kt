package com.code028.inventoryapp.data.repository

import android.net.Uri
import com.code028.inventoryapp.R
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val inventoryRef = firestore.collection("inventory")
    fun register(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val user = User(uid, email, name)
                    firestore.collection("users").document(uid).set(user)

                    // 🔹 Postavi displayName u FirebaseAuth profilu
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)

                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                onResult(it.isSuccessful, it.exception?.message)
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUser() = auth.currentUser

    // ❌ Doesn't works cuz of firestore free pay tier ❌
    fun uploadUserProfileImage(userId: String, uri: Uri, onResult: (Boolean, String?) -> Unit) {
        val ref = storage.reference.child("profile_images/$userId.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    // упиши у Firestore
                    firestore.collection("users").document(userId)
                        .update("profileImageUrl", downloadUri.toString())

                    onResult(true, downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }


    // Solution with basic url of profile image
    fun updateUserProfileImageUrl(userId: String, imageUrl: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(userId)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }


    fun getUserData(userId: String, onResult: (User?) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getUserItems(userId: String, onResult: (List<Equipment>) -> Unit) {
        inventoryRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.toObjects(Equipment::class.java)
                onResult(items)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
