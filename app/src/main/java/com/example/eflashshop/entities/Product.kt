package com.example.eflashshop.entities

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?,
    val categoryId: Long,
    val sellerUserId: Long,
    val imageRef: String?,
    val isListed: Boolean,
    val stock: Int = 0
)
