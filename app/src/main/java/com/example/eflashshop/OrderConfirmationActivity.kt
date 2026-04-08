package com.example.eflashshop

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val orderID = intent.getIntExtra("orderID", 0)
        val orderTotal = intent.getDoubleExtra("orderTotal", 0.0)
        val paymentStatus = intent.getStringExtra("paymentStatus") ?: "UNKNOWN"

        val orderIDText = findViewById<TextView>(R.id.orderIDText)
        val orderDateText = findViewById<TextView>(R.id.orderDateText)
        val orderTotalText = findViewById<TextView>(R.id.orderTotalConfirmText)
        val paymentStatusText = findViewById<TextView>(R.id.paymentStatusText)
        val backToHomeButton = findViewById<Button>(R.id.backToHomeButton)

        val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        orderIDText.text = "Order ID: #$orderID"
        orderDateText.text = "Date: $currentDate"
        orderTotalText.text = "Total: $${String.format("%.2f", orderTotal)}"
        paymentStatusText.text = "Payment: $paymentStatus"

        backToHomeButton.setOnClickListener {
            finish()
        }
    }
}