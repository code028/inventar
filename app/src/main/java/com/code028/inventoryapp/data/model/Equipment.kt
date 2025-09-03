package com.code028.inventoryapp.data.model

data class Equipment(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val location: String = "",
    val description: String = "",
    val status: Boolean = false,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
