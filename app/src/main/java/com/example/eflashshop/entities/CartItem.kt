package com.example.eflashshop.entities

data class CartItem(
    val id: Long,
    val productId: Long = 0,
    val cartId: Long = 0,
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int,
)
