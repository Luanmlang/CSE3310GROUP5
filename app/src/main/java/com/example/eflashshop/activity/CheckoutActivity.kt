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
import com.example.eflashshop.entities.Cart
import com.example.eflashshop.entities.Payment

class CheckoutActivity : AppCompatActivity() {
    private lateinit var checkoutManager: CheckoutManager
    private var cart: Cart? = null
    private lateinit var orderTotalText: TextView
    private lateinit var payButton: Button
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        checkoutManager = CheckoutManager(DatabaseHelper(this))
        val cartId = intent.getLongExtra("cartId", -1L)
        cart = if (cartId > 0) checkoutManager.getCartById(cartId) else checkoutManager.getOrCreateActiveCart()

        orderTotalText = findViewById(R.id.orderTotalText)
        val cardNumberInput = findViewById<EditText>(R.id.cardNumberInput)
        val expiryInput = findViewById<EditText>(R.id.expiryInput)
        val cvvInput = findViewById<EditText>(R.id.cvvInput)
        val cardHolderInput = findViewById<EditText>(R.id.cardHolderInput)
        payButton = findViewById(R.id.payButton)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)

        orderTotalText.text = "Order Total: $${String.format("%.2f", cart?.getTotal() ?: 0.0)}"

        val initialCart = cart
        if (initialCart == null || initialCart.items.isEmpty()) {
            payButton.isEnabled = false
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
        }
        updateCartBar()

        btnBack.setOnClickListener { finish() }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
        btnSearch.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }

        payButton.setOnClickListener {
            val activeCart = cart
            if (activeCart == null || activeCart.items.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val totalAmount = activeCart.getTotal()

            val cardNumber = cardNumberInput.text.toString()
            val expiry = expiryInput.text.toString()
            val cvv = cvvInput.text.toString()
            val cardHolder = cardHolderInput.text.toString()

            // Validate the inputs
            if (cardNumber.length != 16) {
                Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (expiry.length != 5 || !expiry.contains("/")) {
                Toast.makeText(this, "Expiry must be in MM/YY format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cvv.length != 3) {
                Toast.makeText(this, "CVV must be 3 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cardHolder.isEmpty()) {
                Toast.makeText(this, "Please enter cardholder name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create payment and process it
            val payment = Payment(
                paymentID = 1,
                amount = totalAmount,
                paymentMethod = "Credit Card",
                paymentStatus = "PENDING"
            )

            val success = payment.processPayment()

            if (success) {
                val checkoutResult = checkoutManager.createOrderFromCart(userId = 1, cart = activeCart)
                if (checkoutResult == null) {
                    Toast.makeText(this, "Unable to create order", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val itemCount = checkoutResult.orderItems.sumOf { it.quantity }

                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.putExtra("orderID", checkoutResult.order.id)
                intent.putExtra("orderTotal", checkoutResult.order.totalPrice)
                intent.putExtra("paymentStatus", payment.paymentStatus)
                intent.putExtra("itemCount", itemCount)
                intent.putExtra("createdAt", checkoutResult.order.createdAt)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Payment declined. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val cartId = intent.getLongExtra("cartId", -1L)
        cart = if (cartId > 0) checkoutManager.getCartById(cartId) else checkoutManager.getOrCreateActiveCart()
        val totalAmount = cart?.getTotal() ?: 0.0
        orderTotalText.text = "Order Total: $${String.format("%.2f", totalAmount)}"
        payButton.isEnabled = !(cart?.items.isNullOrEmpty())
        updateCartBar()
    }

    private fun updateCartBar() {
        val count = cart?.items?.sumOf { it.quantity } ?: 0
        tvCartBadge.text = if (count == 1) "1 item" else "$count items"
        tvCartSummary.text = "View Cart • $${String.format("%.2f", cart?.getTotal() ?: 0.0)}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}
