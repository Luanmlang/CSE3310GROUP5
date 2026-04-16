package com.example.eflashshop.checkout

import android.content.ContentValues
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.entities.Cart
import com.example.eflashshop.entities.CartItem
import com.example.eflashshop.entities.Order
import com.example.eflashshop.entities.OrderItem
import com.example.eflashshop.entities.Status
import com.example.eflashshop.login.AuthStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CheckoutResult(
    val order: Order,
    val orderItems: List<OrderItem>
)

class CheckoutManager(private val dbHelper: DatabaseHelper) {
    fun getOrCreateActiveCart(): Cart {
        val mappedCartId = AuthStore.getActiveCartId(dbHelper.appContext)
        if (mappedCartId != null && cartExists(mappedCartId)) {
            return getCartById(mappedCartId) ?: Cart(id = mappedCartId)
        }

        val newCartId = createCartId()
        AuthStore.setActiveCartId(dbHelper.appContext, newCartId)
        return getCartById(newCartId) ?: Cart(id = newCartId)
    }

    fun getCartById(cartId: Long): Cart? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CART,
            null,
            "${DatabaseHelper.COLUMN_CART_ID} = ?",
            arrayOf(cartId.toString()),
            null,
            null,
            null
        )

        val cart = if (cursor.moveToFirst()) {
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_DATE_CREATED))
            Cart(
                id = cartId,
                createdAt = createdAt,
                items = getCartItems(cartId)
            )
        } else {
            null
        }

        cursor.close()
        return cart
    }

    fun removeCartItem(cartItemId: Long) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_CART_ITEM,
            "${DatabaseHelper.COLUMN_CART_ITEM_ID} = ?",
            arrayOf(cartItemId.toString())
        )
    }

    fun createOrderFromCart(userId: Long, cart: Cart): CheckoutResult? {
        if (cart.items.isEmpty()) return null

        val db = dbHelper.writableDatabase
        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val orderValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId)
            put(DatabaseHelper.COLUMN_ORDER_STATUS, Status.COMPLETED.name)
            put(DatabaseHelper.COLUMN_ORDER_CREATED_AT, createdAt)
            put(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE, cart.getTotal())
        }
        val orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, orderValues)
        if (orderId == -1L) return null

        val orderItems = cart.items.map { item ->
            val totalPrice = item.price * item.quantity
            val orderItemValues = ContentValues().apply {
                put(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID, orderId)
                put(DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID, item.productId)
                put(DatabaseHelper.COLUMN_ORDER_ITEM_UNIT_PRICE, item.price)
                put(DatabaseHelper.COLUMN_ORDER_ITEM_QUANTITY, item.quantity)
                put(DatabaseHelper.COLUMN_ORDER_ITEM_TOTAL_PRICE, totalPrice)
            }
            val orderItemId = db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, orderItemValues)
            OrderItem(
                id = if (orderItemId == -1L) 0 else orderItemId,
                orderId = orderId,
                productId = item.productId,
                unitPrice = item.price,
                quantity = item.quantity,
                totalPrice = totalPrice
            )
        }

        clearCartItems(cart.id)

        val order = Order(
            id = orderId,
            userId = userId,
            status = Status.COMPLETED,
            createdAt = createdAt,
            totalPrice = cart.getTotal()
        )
        return CheckoutResult(order, orderItems)
    }

    private fun getCartItems(cartId: Long): MutableList<CartItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT 
                ci.${DatabaseHelper.COLUMN_CART_ITEM_ID} AS cart_item_id,
                ci.${DatabaseHelper.COLUMN_CART_ITEM_PRODUCT_ID} AS product_id,
                ci.${DatabaseHelper.COLUMN_CART_ITEM_CART_ID} AS cart_id,
                ci.${DatabaseHelper.COLUMN_CART_ITEM_QUANTITY} AS quantity,
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME} AS product_name,
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE} AS product_price
            FROM ${DatabaseHelper.TABLE_CART_ITEM} ci
            INNER JOIN ${DatabaseHelper.TABLE_PRODUCTS} p 
                ON ci.${DatabaseHelper.COLUMN_CART_ITEM_PRODUCT_ID} = p.${DatabaseHelper.COLUMN_PRODUCT_ID}
            WHERE ci.${DatabaseHelper.COLUMN_CART_ITEM_CART_ID} = ?
            ORDER BY ci.${DatabaseHelper.COLUMN_CART_ITEM_ID} ASC
            """.trimIndent(),
            arrayOf(cartId.toString())
        )

        val items = mutableListOf<CartItem>()
        while (cursor.moveToNext()) {
            items.add(
                CartItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("cart_item_id")),
                    productId = cursor.getLong(cursor.getColumnIndexOrThrow("product_id")),
                    cartId = cursor.getLong(cursor.getColumnIndexOrThrow("cart_id")),
                    productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name")),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("product_price")),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                )
            )
        }
        cursor.close()
        return items
    }

    private fun clearCartItems(cartId: Long) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_CART_ITEM,
            "${DatabaseHelper.COLUMN_CART_ITEM_CART_ID} = ?",
            arrayOf(cartId.toString())
        )
    }

    private fun cartExists(cartId: Long): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CART,
            arrayOf(DatabaseHelper.COLUMN_CART_ID),
            "${DatabaseHelper.COLUMN_CART_ID} = ?",
            arrayOf(cartId.toString()),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun createCartId(): Long {
        val db = dbHelper.writableDatabase
        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_DATE_CREATED, createdAt)
        }
        return db.insert(DatabaseHelper.TABLE_CART, null, values)
    }
}
