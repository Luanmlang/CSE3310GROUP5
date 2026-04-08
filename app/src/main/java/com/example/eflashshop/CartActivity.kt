package com.example.eflashshop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var cart: Cart
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Create a cart with some sample items for testing
        cart = Cart(1)
        cart.addItem(CartItem(1, "Wireless Headphones", 49.99, 1))
        cart.addItem(CartItem(2, "Phone Case", 19.99, 2))
        cart.addItem(CartItem(3, "USB Cable", 9.99, 3))

        totalTextView = findViewById(R.id.totalTextView)
        val checkoutButton = findViewById<Button>(R.id.checkoutButton)
        val recyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)

        cartAdapter = CartAdapter(cart.items) { itemId ->
            cart.removeItem(itemId)
            cartAdapter.notifyDataSetChanged()
            updateTotal()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        updateTotal()

        checkoutButton.setOnClickListener {
            if (cart.checkout()) {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("totalAmount", cart.getTotal())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        totalTextView.text = "Total: $${String.format("%.2f", cart.getTotal())}"
    }
}