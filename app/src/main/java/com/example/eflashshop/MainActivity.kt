package com.example.eflashshop

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eflashshop.entities.Product

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        dbHelper.writableDatabase
        dbHelper.insertSampleDataIfNeeded()

        val searchBar = findViewById<EditText>(R.id.search_bar)
        val searchButton = findViewById<Button>(R.id.search_button)

        loadProducts()

        searchButton.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isBlank()) {
                loadProducts()
            } else {
                loadProducts(query)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadProducts(searchQuery: String = "") {
        val products: List<Product> = if (searchQuery.isBlank()) {
            dbHelper.getFirstFiveProducts()
        } else {
            dbHelper.searchFirstFiveProducts(searchQuery)
        }

        val rowIds = listOf(
            R.id.product_row1,
            R.id.product_row2,
            R.id.product_row3,
            R.id.product_row4,
            R.id.product_row5
        )

        val imageIds = listOf(
            R.id.product_image1,
            R.id.product_image2,
            R.id.product_image3,
            R.id.product_image4,
            R.id.product_image5
        )

        val nameIds = listOf(
            R.id.product_name1,
            R.id.product_name2,
            R.id.product_name3,
            R.id.product_name4,
            R.id.product_name5
        )

        val priceIds = listOf(
            R.id.product_price1,
            R.id.product_price2,
            R.id.product_price3,
            R.id.product_price4,
            R.id.product_price5
        )

        for (i in rowIds.indices) {
            val row = findViewById<View>(rowIds[i])
            val imageView = findViewById<ImageButton>(imageIds[i])
            val nameView = findViewById<TextView>(nameIds[i])
            val priceView = findViewById<TextView>(priceIds[i])

            if (i < products.size) {
                val product = products[i]

                row.visibility = View.VISIBLE
                nameView.text = product.name
                priceView.text = "$%.2f".format(product.price)

                val imageResId = resources.getIdentifier(
                    product.imageName,
                    "drawable",
                    packageName
                )

                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher)
                }
            } else {
                row.visibility = View.GONE
            }
        }
    }
}