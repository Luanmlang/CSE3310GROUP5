package com.example.eflashshop.repository

import android.content.ContentValues
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.login.AuthStore
import java.text.SimpleDateFormat
import java.util.*

class CartRepository(private val dbHelper: DatabaseHelper) {

    fun createCart(): Long {
        val db = dbHelper.writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_DATE_CREATED, currentDate)
        }

        return db.insert(DatabaseHelper.TABLE_CART, null, values)
    }

    fun addItemToCart(cartId: Long, productId: Long, quantity: Int): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_ITEM_PRODUCT_ID, productId)
            put(DatabaseHelper.COLUMN_CART_ITEM_CART_ID, cartId)
            put(DatabaseHelper.COLUMN_CART_ITEM_QUANTITY, quantity)
        }

        return db.insert(DatabaseHelper.TABLE_CART_ITEM, null, values)
    }

    fun getCartItemCount(cartId: Long): Int {
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT SUM(${DatabaseHelper.COLUMN_CART_ITEM_QUANTITY}) as total FROM ${DatabaseHelper.TABLE_CART_ITEM} WHERE ${DatabaseHelper.COLUMN_CART_ITEM_CART_ID} = ?",
                arrayOf(cartId.toString())
            )

            val count = if (cursor.moveToFirst()) {
                val totalIndex = cursor.getColumnIndex("total")
                cursor.getInt(totalIndex)
            } else {
                0
            }

            cursor.close()
            count
        } catch (e: Exception) {
            0
        }
    }

    fun getOrCreateCart(): Long {
        return try {
            val mappedCartId = AuthStore.getActiveCartId(dbHelper.appContext)
            if (mappedCartId != null && cartExists(mappedCartId)) {
                mappedCartId
            } else {
                val newCartId = createCart()
                AuthStore.setActiveCartId(dbHelper.appContext, newCartId)
                newCartId
            }
        } catch (e: Exception) {
            val newCartId = createCart()
            AuthStore.setActiveCartId(dbHelper.appContext, newCartId)
            newCartId
        }
    }

    fun deleteAllCarts() {
        val cartId = AuthStore.getActiveCartId(dbHelper.appContext) ?: return
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_CART_ITEM,
            "${DatabaseHelper.COLUMN_CART_ITEM_CART_ID} = ?",
            arrayOf(cartId.toString())
        )
        db.delete(
            DatabaseHelper.TABLE_CART,
            "${DatabaseHelper.COLUMN_CART_ID} = ?",
            arrayOf(cartId.toString())
        )
        AuthStore.clearActiveCartForCurrentUser(dbHelper.appContext)
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
}
