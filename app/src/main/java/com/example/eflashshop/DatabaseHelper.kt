package com.example.eflashshop

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.eflashshop.login.AuthStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {
    val appContext: Context = context.applicationContext

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USER (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_ROLE TEXT NOT NULL,
                $COLUMN_USER_PROFILE_IMAGE TEXT,
                $COLUMN_USER_CREATED_AT TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ADDRESS (
                $COLUMN_ADDRESS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADDRESS_STREET TEXT NOT NULL,
                $COLUMN_ADDRESS_CITY TEXT NOT NULL,
                $COLUMN_ADDRESS_STATE TEXT NOT NULL,
                $COLUMN_ADDRESS_ZIP TEXT NOT NULL,
                $COLUMN_ADDRESS_USER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ADDRESS_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CATEGORY (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                $COLUMN_PRODUCT_DESCRIPTION TEXT,
                $COLUMN_PRODUCT_IMAGE_REF TEXT,
                $COLUMN_PRODUCT_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_PRODUCT_SELLER_USER_ID INTEGER NOT NULL,
                $COLUMN_PRODUCT_IS_LISTED INTEGER NOT NULL DEFAULT 1,
                $COLUMN_PRODUCT_STOCK INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY ($COLUMN_PRODUCT_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_CATEGORY_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_PRODUCT_SELLER_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CART (
                $COLUMN_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_DATE_CREATED TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CART_ITEM (
                $COLUMN_CART_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_ITEM_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_CART_ITEM_CART_ID INTEGER NOT NULL,
                $COLUMN_CART_ITEM_QUANTITY INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_CART_ITEM_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_CART_ITEM_CART_ID) REFERENCES $TABLE_CART($COLUMN_CART_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_BUYER_USER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                $COLUMN_ORDER_CREATED_AT TEXT NOT NULL,
                $COLUMN_ORDER_TOTAL_PRICE REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ORDER_BUYER_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ORDER_ITEMS (
                $COLUMN_ORDER_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_ITEM_ORDER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_UNIT_PRICE REAL NOT NULL,
                $COLUMN_ORDER_ITEM_QUANTITY INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_TOTAL_PRICE REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ORDER_ITEM_ORDER_ID) REFERENCES $TABLE_ORDERS($COLUMN_ORDER_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_ORDER_ITEM_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        seedAdminAccount(db)
        AuthStore.resetToAdminOnly(appContext)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $COLUMN_PRODUCT_STOCK INTEGER NOT NULL DEFAULT 0")
            return
        }
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART_ITEM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADDRESS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun resetAllData() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_ORDER_ITEMS, null, null)
            db.delete(TABLE_ORDERS, null, null)
            db.delete(TABLE_CART_ITEM, null, null)
            db.delete(TABLE_CART, null, null)
            db.delete(TABLE_PRODUCTS, null, null)
            db.delete(TABLE_CATEGORY, null, null)
            db.delete(TABLE_ADDRESS, null, null)
            db.delete(TABLE_USER, null, null)
            db.execSQL("DELETE FROM sqlite_sequence")
            seedAdminAccount(db)
            AuthStore.resetToAdminOnly(appContext)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAdminUserId(): Long {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_ROLE = ?",
            arrayOf(DEFAULT_ADMIN_EMAIL, ROLE_ADMIN),
            null,
            null,
            null,
            "1"
        )
        val id = if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
        } else {
            -1L
        }
        cursor.close()
        return id
    }

    private fun seedAdminAccount(db: SQLiteDatabase) {
        val existing = db.query(
            TABLE_USER,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(DEFAULT_ADMIN_EMAIL),
            null,
            null,
            null,
            "1"
        )
        val exists = existing.moveToFirst()
        existing.close()
        if (exists) {
            return
        }

        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_USER_EMAIL, DEFAULT_ADMIN_EMAIL)
            put(COLUMN_USER_NAME, DEFAULT_ADMIN_USERNAME)
            put(COLUMN_USER_ROLE, ROLE_ADMIN)
            put(COLUMN_USER_PROFILE_IMAGE, "ic_profile")
            put(COLUMN_USER_CREATED_AT, createdAt)
        }
        db.insert(TABLE_USER, null, values)
    }

    companion object {
        private const val DATABASE_NAME = "eflashshop.db"
        private const val DATABASE_VERSION = 7

        const val TABLE_USER = "user"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_ROLE = "role"
        const val COLUMN_USER_PROFILE_IMAGE = "profile_image"
        const val COLUMN_USER_CREATED_AT = "createdAt"

        const val TABLE_ADDRESS = "address"
        const val COLUMN_ADDRESS_ID = "id"
        const val COLUMN_ADDRESS_STREET = "street"
        const val COLUMN_ADDRESS_CITY = "city"
        const val COLUMN_ADDRESS_STATE = "state"
        const val COLUMN_ADDRESS_ZIP = "zip"
        const val COLUMN_ADDRESS_USER_ID = "user_id"

        const val TABLE_CATEGORY = "category"
        const val COLUMN_CATEGORY_ID = "id"
        const val COLUMN_CATEGORY_NAME = "name"

        const val TABLE_PRODUCTS = "products"
        const val COLUMN_PRODUCT_ID = "id"
        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_IMAGE_REF = "image_ref"
        const val COLUMN_PRODUCT_CATEGORY_ID = "category_id"
        const val COLUMN_PRODUCT_SELLER_USER_ID = "seller_user_id"
        const val COLUMN_PRODUCT_IS_LISTED = "is_listed"
        const val COLUMN_PRODUCT_STOCK = "stock"
        const val COLUMN_PRODUCT_USER_ID = COLUMN_PRODUCT_SELLER_USER_ID

        const val TABLE_CART = "cart"
        const val COLUMN_CART_ID = "id"
        const val COLUMN_CART_DATE_CREATED = "dateCreated"

        const val TABLE_CART_ITEM = "cart_item"
        const val COLUMN_CART_ITEM_ID = "id"
        const val COLUMN_CART_ITEM_PRODUCT_ID = "product_id"
        const val COLUMN_CART_ITEM_CART_ID = "cart_id"
        const val COLUMN_CART_ITEM_QUANTITY = "quantity"

        const val TABLE_ORDERS = "orders"
        const val COLUMN_ORDER_ID = "id"
        const val COLUMN_ORDER_BUYER_USER_ID = "buyer_user_id"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_CREATED_AT = "created_at"
        const val COLUMN_ORDER_TOTAL_PRICE = "total_price"
        const val COLUMN_ORDER_USER_ID = COLUMN_ORDER_BUYER_USER_ID

        const val TABLE_ORDER_ITEMS = "order_items"
        const val COLUMN_ORDER_ITEM_ID = "id"
        const val COLUMN_ORDER_ITEM_ORDER_ID = "order_id"
        const val COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id"
        const val COLUMN_ORDER_ITEM_UNIT_PRICE = "unit_price"
        const val COLUMN_ORDER_ITEM_QUANTITY = "quantity"
        const val COLUMN_ORDER_ITEM_TOTAL_PRICE = "total_price"

        const val ROLE_ADMIN = "admin"
        const val DEFAULT_ADMIN_USERNAME = "admin"
        const val DEFAULT_ADMIN_EMAIL = "admin@eflashshop.local"
    }
}
