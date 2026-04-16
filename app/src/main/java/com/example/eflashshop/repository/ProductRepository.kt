package com.example.eflashshop.repository

import android.database.Cursor
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.entities.Product
import com.example.eflashshop.dto.ProductDTO
import com.example.eflashshop.dto.SellerDTO

class ProductRepository(private val dbHelper: DatabaseHelper) {

    fun getFeaturedProducts(limit: Int = 5): List<Product> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            "${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED} = 1",
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} ASC",
            limit.toString()
        )

        val products = mutableListOf<Product>()
        while (cursor.moveToNext()) {
            products.add(mapCursorToProduct(cursor))
        }
        cursor.close()
        return products
    }

    fun searchProducts(searchQuery: String = ""): List<Product> {
        val db = dbHelper.readableDatabase
        val hasQuery = searchQuery.isNotBlank()
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            if (hasQuery) {
                "(${DatabaseHelper.COLUMN_PRODUCT_NAME} LIKE ? OR ${DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION} LIKE ?) AND ${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED} = 1"
            } else {
                "${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED} = 1"
            },
            if (hasQuery) arrayOf("%$searchQuery%", "%$searchQuery%") else null,
            null,
            null,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} ASC"
        )

        val products = mutableListOf<Product>()
        while (cursor.moveToNext()) {
            products.add(mapCursorToProduct(cursor))
        }
        cursor.close()
        return products
    }

    fun getProductDetailById(productId: Long): ProductDTO? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT
                p.${DatabaseHelper.COLUMN_PRODUCT_ID},
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME},
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE},
                p.${DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION},
                p.${DatabaseHelper.COLUMN_PRODUCT_IMAGE_REF},
                p.${DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID},
                p.${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID},
                p.${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED},
                c.${DatabaseHelper.COLUMN_CATEGORY_NAME} AS category_name,
                u.${DatabaseHelper.COLUMN_USER_ID} AS seller_id,
                u.${DatabaseHelper.COLUMN_USER_NAME} AS seller_name,
                u.${DatabaseHelper.COLUMN_USER_EMAIL} AS seller_email,
                u.${DatabaseHelper.COLUMN_USER_PROFILE_IMAGE} AS seller_profile_image
            FROM ${DatabaseHelper.TABLE_PRODUCTS} p
            INNER JOIN ${DatabaseHelper.TABLE_CATEGORY} c
                ON p.${DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
            INNER JOIN ${DatabaseHelper.TABLE_USER} u
                ON p.${DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID} = u.${DatabaseHelper.COLUMN_USER_ID}
            WHERE p.${DatabaseHelper.COLUMN_PRODUCT_ID} = ?
                AND p.${DatabaseHelper.COLUMN_PRODUCT_IS_LISTED} = 1
            """.trimIndent(),
            arrayOf(productId.toString())
        )

        val detail = if (cursor.moveToFirst()) {
            val product = mapCursorToProduct(cursor)
            val seller = SellerDTO(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("seller_id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("seller_name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("seller_email")),
                profileImageRef = cursor.getString(cursor.getColumnIndexOrThrow("seller_profile_image"))
            )
            val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
            ProductDTO(product = product, categoryName = categoryName, sellerDTO = seller)
        } else {
            null
        }

        cursor.close()
        return detail
    }

    fun getProductById(productId: Long): Product? {
        return getProductDetailById(productId)?.product
    }

    fun getCategoryNameById(categoryId: Long): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CATEGORY,
            arrayOf(DatabaseHelper.COLUMN_CATEGORY_NAME),
            "${DatabaseHelper.COLUMN_CATEGORY_ID} = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            null
        )

        val categoryName = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME))
        } else {
            null
        }
        cursor.close()
        return categoryName
    }

    private fun mapCursorToProduct(cursor: Cursor): Product {
        return Product(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
            categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID)),
            sellerUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SELLER_USER_ID)),
            imageRef = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_REF)),
            isListed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IS_LISTED)) == 1
        )
    }
}
