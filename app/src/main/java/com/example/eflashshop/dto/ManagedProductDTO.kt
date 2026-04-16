package com.example.eflashshop.dto

data class ManagedProductDTO(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?,
    val imageRef: String?,
    val categoryName: String,
    val sellerName: String,
    val isListed: Boolean
)