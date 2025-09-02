package com.code028.inventoryapp.data.repository

import com.code028.inventoryapp.data.model.Equipment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val inventoryRef = firestore.collection("inventory")

    fun addItem(equipment: Equipment, userId: String) =
        inventoryRef.document(equipment.id.ifEmpty { inventoryRef.document().id }).set(equipment.copy(userId = userId))

    fun getItems() = inventoryRef.orderBy("createdAt", Query.Direction.DESCENDING)

    fun getItem(id: String) = inventoryRef.document(id).get()

    fun updateItem(id: String, data: Map<String, Any>) =
        inventoryRef.document(id).update(data)

    fun deleteItem(id: String) = inventoryRef.document(id).delete()

}
