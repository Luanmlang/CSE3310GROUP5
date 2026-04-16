package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.entities.Product
import com.example.eflashshop.login.AuthStore

class HomePageActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var searchBar: EditText
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        checkoutManager = CheckoutManager(dbHelper)
        searchBar = findViewById(R.id.search_bar)
        val searchButton = findViewById<Button>(R.id.search_button)
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        val title = findViewById<TextView>(R.id.title)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)
        title.text = "eFlashShop - ${AuthStore.getCurrentUser(this) ?: "Guest"}"

        searchButton.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isBlank()) {
                loadProducts()
            } else {
                loadProducts(query)
            }
        }

        btnHome.setOnClickListener {
            searchBar.setText("")
            loadProducts()
        }

        btnSearch.setOnClickListener {
            searchBar.requestFocus()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        logoutButton.setOnClickListener {
            AuthStore.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadProducts()
        updateCartBar()
    }

    override fun onResume() {
        super.onResume()
        updateCartBar()
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

                imageView.setImageResource(resolveImageFor(product))
                imageView.setOnClickListener {
                    val intent = Intent(this, ItemDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    startActivity(intent)
                }
                row.setOnClickListener {
                    val intent = Intent(this, ItemDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    startActivity(intent)
                }
            } else {
                row.visibility = View.GONE
                row.setOnClickListener(null)
                imageView.setOnClickListener(null)
            }
        }

        if (products.isEmpty()) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resolveImageFor(product: Product): Int {
        return when {
            product.name.contains("headphones", ignoreCase = true) -> R.drawable.headphones_playstore
            product.name.contains("mouse", ignoreCase = true) -> R.drawable.mouse
            product.name.contains("watch", ignoreCase = true) -> R.drawable.watch
            else -> R.drawable.image_placeholder
        }
    }

    private fun updateCartBar() {
        val activeCart = checkoutManager.getOrCreateActiveCart()
        val count = activeCart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count == 1) "1 item" else "$count items"
        tvCartSummary.text = "View Cart • $${String.format("%.2f", activeCart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}
