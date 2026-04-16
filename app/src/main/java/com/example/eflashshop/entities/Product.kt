package com.example.eflashshop.entities

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String?,
    val categoryId: Long,
    val userId: Long,
    val imageName: String
)