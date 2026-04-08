package com.example.eflashshop

data class Cart(
    val id: Int,
    val items: MutableList<CartItem> = mutableListOf()
) {
    fun addItem(item: CartItem) {
        items.add(item)
    }

    fun removeItem(itemId: Int) {
        val item = items.find { it.id == itemId }
        if (item != null) {
            if (item.quantity > 1) {
                val index = items.indexOf(item)
                items[index] = item.copy(quantity = item.quantity - 1)
            }
            else {
                items.remove(item)
            }
        }
    }

    fun getTotal(): Double {
        var total = 0.0
        for (item in items) {
            total += item.price * item.quantity
        }
        return total
    }

    fun checkout(): Boolean {
        return items.isNotEmpty()
    }
}