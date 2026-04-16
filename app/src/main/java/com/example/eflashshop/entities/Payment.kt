package com.example.eflashshop.entities

data class Payment(
    val paymentID: Int,
    val amount: Double,
    var paymentMethod: String,
    var paymentStatus: String
) {
    fun processPayment(): Boolean {
        // Mock payment - just checks if the amount is greater than 0
        return if (amount > 0) {
            paymentStatus = "SUCCESS"
            true
        } else {
            paymentStatus = "DECLINED"
            false
        }
    }

    fun refund(): Boolean {
        paymentStatus = "REFUNDED"
        return true
    }
}