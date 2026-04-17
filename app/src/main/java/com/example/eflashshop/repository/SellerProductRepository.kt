package com.example.eflashshop.repository

import android.content.ContentValues
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.dto.ManagedProductDTO

class SellerProductRepository(private val dbHelper: DatabaseHelper) {

    fun getProductsForSeller(sellerEmail: String): List<ManagedProductDTO> {
        val db = dbHelper.readableDatabase
        val sellerId = getSellerIdByEmail(db, sellerEmail) ?: return emptyList()
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
            WHERE p.${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID} = ?
            ORDER BY p.${DatabaseHelper.COLUMN_PRODUCT_ID} DESC
            """.trimIndent(),
            arrayOf(sellerId.toString())
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

    fun setProductListed(productId: Long, sellerEmail: String, isListed: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val sellerId = getSellerIdByEmail(db, sellerEmail) ?: return false
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_IS_LISTED, if (isListed) 1 else 0)
        }
        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ? AND ${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID} = ?",
            arrayOf(productId.toString(), sellerId.toString())
        )
        return rowsUpdated > 0
    }

    fun deleteProduct(productId: Long, sellerEmail: String): Boolean {
        val db = dbHelper.writableDatabase
        val sellerId = getSellerIdByEmail(db, sellerEmail) ?: return false
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ? AND ${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID} = ?",
            arrayOf(productId.toString(), sellerId.toString())
        )
        return rowsDeleted > 0
    }

    private fun getSellerIdByEmail(db: android.database.sqlite.SQLiteDatabase, email: String): Long? {
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(email.trim().lowercase()),
            null, null, null, "1"
        )
        val id = if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
        } else null
        cursor.close()
        return id
    }
}
