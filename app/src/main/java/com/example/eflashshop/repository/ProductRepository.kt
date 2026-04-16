package com.example.eflashshop.repository

import android.database.Cursor
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.entities.Product

class ProductRepository(private val dbHelper: DatabaseHelper) {

    fun getProductById(productId: Long): Product? {
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
                arrayOf(productId.toString()),
                null,
                null,
                null
            )

            val product = if (cursor.moveToFirst()) {
                mapCursorToProduct(cursor)
            } else {
                null
            }

            cursor.close()
            product
        } catch (e: Exception) {
            null
        }
    }

    fun getCategoryNameById(categoryId: Long): String? {
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.TABLE_CATEGORY,
                null,
                "${DatabaseHelper.COLUMN_CATEGORY_ID} = ?",
                arrayOf(categoryId.toString()),
                null,
                null,
                null
            )

            val categoryName = if (cursor.moveToFirst()) {
                val categoryNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME)
                cursor.getString(categoryNameIndex)
            } else {
                null
            }

            cursor.close()
            categoryName
        } catch (e: Exception) {
            null
        }
    }

    private fun mapCursorToProduct(cursor: Cursor): Product {
        val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)
        val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)
        val priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE)
        val descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)
        val categoryIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID)
        val userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_USER_ID)

        return Product(
            id = cursor.getLong(idIndex),
            name = cursor.getString(nameIndex),
            price = cursor.getDouble(priceIndex),
            description = cursor.getString(descIndex),
            categoryId = cursor.getLong(categoryIdIndex),
            userId = cursor.getLong(userIdIndex)
        )
    }
}
