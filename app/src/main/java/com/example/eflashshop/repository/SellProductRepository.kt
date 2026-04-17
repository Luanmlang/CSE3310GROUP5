package com.example.eflashshop.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.login.AuthStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SellProductRepository(private val dbHelper: DatabaseHelper) {

    fun addProductForSeller(
        sellerName: String,
        sellerEmail: String,
        productName: String,
        productPrice: Double,
        categoryName: String,
        description: String?,
        imageRef: String?,
        stock: Int
    ): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        return try {
            val sellerId = getOrCreateSellerId(db, sellerName, sellerEmail)
            val categoryId = getOrCreateCategoryId(db, categoryName)
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_PRODUCT_NAME, productName)
                put(DatabaseHelper.COLUMN_PRODUCT_PRICE, productPrice)
                put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description)
                put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_REF, imageRef)
                put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID, categoryId)
                put(DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID, sellerId)
                put(DatabaseHelper.COLUMN_PRODUCT_IS_LISTED, 1)
                put(DatabaseHelper.COLUMN_PRODUCT_STOCK, stock)
            }
            val productId = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
            if (productId > 0) {
                db.setTransactionSuccessful()
            }
            productId
        } finally {
            db.endTransaction()
        }
    }

    private fun getOrCreateSellerId(db: SQLiteDatabase, sellerName: String, sellerEmail: String): Long {
        val normalizedEmail = sellerEmail.trim().lowercase()
        val normalizedName = sellerName.trim().ifBlank { normalizedEmail.substringBefore("@") }
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.COLUMN_USER_ID, DatabaseHelper.COLUMN_USER_NAME),
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(normalizedEmail),
            null,
            null,
            null,
            "1"
        )
        val existingId = if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
            val existingName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME))
            if (existingName != normalizedName) {
                val updateValues = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_USER_NAME, normalizedName)
                }
                db.update(
                    DatabaseHelper.TABLE_USER,
                    updateValues,
                    "${DatabaseHelper.COLUMN_USER_ID} = ?",
                    arrayOf(userId.toString())
                )
            }
            userId
        } else {
            -1L
        }
        cursor.close()
        if (existingId > 0) {
            return existingId
        }

        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_EMAIL, normalizedEmail)
            put(DatabaseHelper.COLUMN_USER_NAME, normalizedName)
            put(
                DatabaseHelper.COLUMN_USER_ROLE,
                if (normalizedEmail == AuthStore.ADMIN_EMAIL) DatabaseHelper.ROLE_ADMIN else AuthStore.ROLE_BUYER
            )
            put(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE, "ic_profile")
            put(DatabaseHelper.COLUMN_USER_CREATED_AT, createdAt)
        }
        return db.insert(DatabaseHelper.TABLE_USER, null, values)
    }

    private fun getOrCreateCategoryId(db: SQLiteDatabase, categoryName: String): Long {
        val normalizedCategory = categoryName.trim()
        val cursor = db.query(
            DatabaseHelper.TABLE_CATEGORY,
            arrayOf(DatabaseHelper.COLUMN_CATEGORY_ID),
            "${DatabaseHelper.COLUMN_CATEGORY_NAME} = ? COLLATE NOCASE",
            arrayOf(normalizedCategory),
            null,
            null,
            null,
            "1"
        )
        val categoryId = if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID))
        } else {
            -1L
        }
        cursor.close()
        if (categoryId > 0) {
            return categoryId
        }

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CATEGORY_NAME, normalizedCategory)
        }
        return db.insert(DatabaseHelper.TABLE_CATEGORY, null, values)
    }
}
