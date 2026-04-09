package com.example.eflashshop.entities

data class OrderItem(
    val id: Long,
    val orderId: Long,
    val productId: Long,
    val unitPrice: Double,
    val quantity: Int,
    val totalPrice: Double
){
    fun calculatedPrice(quantity: Int, unitPrice: Double): Double{
        return quantity*unitPrice;
    }
}
