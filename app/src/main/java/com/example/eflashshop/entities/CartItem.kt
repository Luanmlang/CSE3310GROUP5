package com.example.eflashshop.entities

data class CartItem(
    val id: Long,
    val productId: Long,
    val cartId: Long,
    val quantity: Int,
)
