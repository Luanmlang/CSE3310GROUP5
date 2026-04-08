package com.example.eflashshop

import java.util.Date

data class Order(
    val orderID: Int,
    val orderDate: Date,
    val total: Double,
    var status: Boolean,
    val items: List<CartItem>
)