package com.example.eflashshop.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Cart(
    val id: Long,
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val items: MutableList<CartItem> = mutableListOf()
) {
    fun addItem(item: CartItem) {
        items.add(item)
    }

    fun removeItem(itemId: Long) {
        items.removeAll { it.id == itemId }
    }

    fun getTotal(): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun checkout(): Boolean {
        return items.isNotEmpty()
    }
}
