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
        val item = items.find { it.id == itemId }
        if (item != null) {
            if (item.quantity > 1) {
                val index = items.indexOf(item)
                items[index] = item.copy(quantity = item.quantity - 1)
            } else {
                items.remove(item)
            }
        }
    }

    fun getTotal(): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun checkout(): Boolean {
        return items.isNotEmpty()
    }
}
