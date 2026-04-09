package com.example.eflashshop.entities

data class Order(
    val id: Long,
    val userId: Long,
    val status: Status,
    val createdAt: String,
    val totalPrice: Double
)
