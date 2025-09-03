package com.code028.inventoryapp.data.repository

import com.code028.inventoryapp.data.model.Equipment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val inventoryRef = firestore.collection("inventory")

    fun addItem(equipment: Equipment, userId: String): Task<Void> {
        val docRef = inventoryRef.document(equipment.id.ifEmpty { inventoryRef.document().id })
        val newId = docRef.id
        return docRef.set(
            equipment.copy(
                id = newId,
                userId = userId,
                createdAt = System.currentTimeMillis()
            )
        )
    }


    fun getUserItems(userId: String) =
        inventoryRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

    fun getItem(id: String) = inventoryRef.document(id).get()

    fun updateItem(id: String, data: Map<String, Any>) =
        inventoryRef.document(id).update(data)

    fun deleteItem(id: String) = inventoryRef.document(id).delete()

    fun getAllUserCategories(userId: String, onResult: (List<String>) -> Unit) {
        inventoryRef.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val categories = snapshot.documents
                    .mapNotNull { it.getString("category")?.trim() }
                    .distinct()
                onResult(categories)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getAllUserLocations(userId: String, onResult: (List<String>) -> Unit) {
        inventoryRef.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val locations = snapshot.documents
                    .mapNotNull { it.getString("location")?.trim() }
                    .distinct()
                onResult(locations)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}
