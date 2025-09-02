package com.code028.inventoryapp.ui.inventory

import androidx.lifecycle.ViewModel
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.data.repository.AuthRepository
import com.code028.inventoryapp.data.repository.InventoryRepository
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repo: InventoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun addItem(item: Equipment): Task<Void>? {
        val uid = authRepository.currentUser()?.uid ?: return null
        return repo.addItem(item, uid)
    }

    fun getItems() = repo.getItems()
    fun getItem(id: String) = repo.getItem(id)
    fun updateItem(id: String, data: Map<String, Any>) = repo.updateItem(id, data)
    fun deleteItem(id: String) = repo.deleteItem(id)
}
