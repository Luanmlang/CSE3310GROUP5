package com.example.eflashshop.entities

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?,
    val categoryId: Long,
    val userId: Long
)