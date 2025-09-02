package com.code028.inventoryapp.data.model

data class Equipment(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val description: String = "",
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
