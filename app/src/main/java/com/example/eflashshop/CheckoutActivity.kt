package com.example.eflashshop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Date

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        val orderTotalText = findViewById<TextView>(R.id.orderTotalText)
        val cardNumberInput = findViewById<EditText>(R.id.cardNumberInput)
        val expiryInput = findViewById<EditText>(R.id.expiryInput)
        val cvvInput = findViewById<EditText>(R.id.cvvInput)
        val cardHolderInput = findViewById<EditText>(R.id.cardHolderInput)
        val payButton = findViewById<Button>(R.id.payButton)

        orderTotalText.text = "Order Total: $${String.format("%.2f", totalAmount)}"

        payButton.setOnClickListener {
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
                // Create the order
                val order = Order(
                    orderID = (1..99999).random(),
                    orderDate = Date(),
                    total = totalAmount,
                    status = true,
                    items = listOf()
                )

                // Go to confirmation screen
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.putExtra("orderID", order.orderID)
                intent.putExtra("orderTotal", order.total)
                intent.putExtra("paymentStatus", payment.paymentStatus)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Payment declined. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}