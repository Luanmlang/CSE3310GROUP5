package com.example.eflashshop.repository

import android.content.ContentValues
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.dto.ManagedProductDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminProductRepository(private val dbHelper: DatabaseHelper) {

    fun getManagedProducts(): List<ManagedProductDTO> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT
                p.${DatabaseHelper.COLUMN_PRODUCT_ID} AS product_id,
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME} AS product_name,
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE} AS product_price,
                p.${DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION} AS product_description,
                p.${DatabaseHelper.COLUMN_PRODUCT_IMAGE_REF} AS product_image_ref,
                p.${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED} AS product_is_listed,
                p.${DatabaseHelper.COLUMN_PRODUCT_STOCK} AS product_stock,
                COALESCE(c.${DatabaseHelper.COLUMN_CATEGORY_NAME}, 'Uncategorized') AS category_name,
                COALESCE(u.${DatabaseHelper.COLUMN_USER_NAME}, 'Unknown Seller') AS seller_name
            FROM ${DatabaseHelper.TABLE_PRODUCTS} p
            LEFT JOIN ${DatabaseHelper.TABLE_CATEGORY} c
                ON p.${DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
            LEFT JOIN ${DatabaseHelper.TABLE_USER} u
                ON p.${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID} = u.${DatabaseHelper.COLUMN_USER_ID}
            ORDER BY p.${DatabaseHelper.COLUMN_PRODUCT_ID} DESC
            """.trimIndent(),
            null
        )

        val products = mutableListOf<ManagedProductDTO>()
        while (cursor.moveToNext()) {
            products.add(
                ManagedProductDTO(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("product_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("product_name")),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("product_price")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("product_description")),
                    imageRef = cursor.getString(cursor.getColumnIndexOrThrow("product_image_ref")),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                    sellerName = cursor.getString(cursor.getColumnIndexOrThrow("seller_name")),
                    isListed = cursor.getInt(cursor.getColumnIndexOrThrow("product_is_listed")) == 1,
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("product_stock"))
                )
            )
        }
        cursor.close()
        return products
    }

    fun addProduct(
        name: String,
        price: Double,
        description: String?,
        categoryName: String,
        imageRef: String?
    ): Long {
        val db = dbHelper.writableDatabase
        val sellerUserId = getOrCreateAdminUserId(db)
        val categoryId = getOrCreateCategoryId(db, categoryName)
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, name)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_REF, imageRef)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID, categoryId)
            put(DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID, sellerUserId)
            put(DatabaseHelper.COLUMN_PRODUCT_IS_LISTED, 1)
        }
        return db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
    }

    fun setProductListed(productId: Long, isListed: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_IS_LISTED, if (isListed) 1 else 0)
        }
        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        return rowsUpdated > 0
    }

    fun deleteProduct(productId: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        return rowsDeleted > 0
    }

    fun resetDatabase(): Boolean {
        return try {
            dbHelper.resetAllData()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun getOrCreateCategoryId(db: android.database.sqlite.SQLiteDatabase, categoryName: String): Long {
        val normalizedCategory = categoryName.trim()
        val cursor = db.query(
            DatabaseHelper.TABLE_CATEGORY,
            arrayOf(DatabaseHelper.COLUMN_CATEGORY_ID),
            "${DatabaseHelper.COLUMN_CATEGORY_NAME} = ?",
            arrayOf(normalizedCategory),
            null,
            null,
            null,
            "1"
        )
        val existingId = if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID))
        } else {
            -1L
        }
        cursor.close()
        if (existingId > 0) {
            return existingId
        }

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CATEGORY_NAME, normalizedCategory)
        }
        return db.insert(DatabaseHelper.TABLE_CATEGORY, null, values)
    }

    private fun getOrCreateAdminUserId(db: android.database.sqlite.SQLiteDatabase): Long {
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(DatabaseHelper.DEFAULT_ADMIN_EMAIL),
            null,
            null,
            null,
            "1"
        )
        val existingId = if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
        } else {
            -1L
        }
        cursor.close()
        if (existingId > 0) {
            return existingId
        }

        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_EMAIL, DatabaseHelper.DEFAULT_ADMIN_EMAIL)
            put(DatabaseHelper.COLUMN_USER_NAME, DatabaseHelper.DEFAULT_ADMIN_USERNAME)
            put(DatabaseHelper.COLUMN_USER_ROLE, DatabaseHelper.ROLE_ADMIN)
            put(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE, "ic_profile")
            put(DatabaseHelper.COLUMN_USER_CREATED_AT, createdAt)
        }
        return db.insert(DatabaseHelper.TABLE_USER, null, values)
    }
}
