package com.example.eflashshop

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
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
                $COLUMN_PRODUCT_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_PRODUCT_USER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PRODUCT_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_CATEGORY_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_PRODUCT_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
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
                $COLUMN_ORDER_USER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                $COLUMN_ORDER_CREATED_AT TEXT NOT NULL,
                $COLUMN_ORDER_TOTAL_PRICE REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ORDER_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID) ON DELETE CASCADE
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

        insertSampleData(db)
    }

    //HARDCODE DATA FOR DATABASE FOR NOW
    private fun insertSampleData(db: SQLiteDatabase) {
        db.execSQL("INSERT INTO $TABLE_USER ($COLUMN_USER_EMAIL, $COLUMN_USER_NAME, $COLUMN_USER_ROLE, $COLUMN_USER_CREATED_AT) VALUES ('seller@example.com', 'Test Seller', 'seller', '2024-01-01')")

        db.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORY_NAME) VALUES ('Electronics')")
        db.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORY_NAME) VALUES ('Fashion')")
        db.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORY_NAME) VALUES ('Home & Garden')")

        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION, $COLUMN_PRODUCT_CATEGORY_ID, $COLUMN_PRODUCT_USER_ID) VALUES ('Wireless Headphones', 99.99, 'Premium wireless headphones', 1, 1)")
        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION, $COLUMN_PRODUCT_CATEGORY_ID, $COLUMN_PRODUCT_USER_ID) VALUES ('USB-C Cable', 15.99, 'High-speed USB-C charging and data cable. Compatible with most modern devices.', 1, 1)")
        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION, $COLUMN_PRODUCT_CATEGORY_ID, $COLUMN_PRODUCT_USER_ID) VALUES ('T-Shirt', 29.99, 'Comfortable cotton T-shirt available in multiple colors and sizes.', 2, 1)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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

    companion object {
        private const val DATABASE_NAME = "eflashshop.db"
        private const val DATABASE_VERSION = 4

        const val TABLE_USER = "user"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_ROLE = "role"
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
        const val COLUMN_PRODUCT_CATEGORY_ID = "category_id"
        const val COLUMN_PRODUCT_USER_ID = "user_id"
        
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
        const val COLUMN_ORDER_USER_ID = "user_id"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_CREATED_AT = "created_at"
        const val COLUMN_ORDER_TOTAL_PRICE = "total_price"
        
        const val TABLE_ORDER_ITEMS = "order_items"
        const val COLUMN_ORDER_ITEM_ID = "id"
        const val COLUMN_ORDER_ITEM_ORDER_ID = "order_id"
        const val COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id"
        const val COLUMN_ORDER_ITEM_UNIT_PRICE = "unit_price"
        const val COLUMN_ORDER_ITEM_QUANTITY = "quantity"
        const val COLUMN_ORDER_ITEM_TOTAL_PRICE = "total_price"
    }
}
