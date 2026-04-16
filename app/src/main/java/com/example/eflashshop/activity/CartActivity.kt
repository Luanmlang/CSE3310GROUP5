package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.entities.CartAdapter
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.entities.Cart

class CartActivity : AppCompatActivity() {
    private lateinit var cart: Cart
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalTextView: TextView
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        checkoutManager = CheckoutManager(DatabaseHelper(this))
        cart = checkoutManager.getOrCreateActiveCart()

        totalTextView = findViewById(R.id.totalTextView)
        val checkoutButton = findViewById<Button>(R.id.checkoutButton)
        val recyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnAddProduct = findViewById<ImageButton>(R.id.btnAddProduct)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)

        cartAdapter = CartAdapter(cart.items) { itemId ->
            checkoutManager.removeCartItem(itemId)
            cart.removeItem(itemId)
            cartAdapter.notifyDataSetChanged()
            updateTotal()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter
        updateTotal()

        btnBack.setOnClickListener { finish() }

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchResultsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SellProductActivity::class.java))
        }

        btnCart.setOnClickListener {
            updateTotal()
        }
        cartBar.setOnClickListener {
            updateTotal()
        }

        checkoutButton.setOnClickListener {
            if (!cart.checkout()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("cartId", cart.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val latestCart = checkoutManager.getOrCreateActiveCart()
        cart.items.clear()
        cart.items.addAll(latestCart.items)
        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateTotal() {
        totalTextView.text = "Total: $${String.format("%.2f", cart.getTotal())}"
        val count = cart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count == 1) "1 item" else "$count items"
        tvCartSummary.text = "Cart • $${String.format("%.2f", cart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}
