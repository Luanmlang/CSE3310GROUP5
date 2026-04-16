package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.login.AuthStore

class ProfileActivity : AppCompatActivity() {
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        checkoutManager = CheckoutManager(DatabaseHelper(this))

        val tvUsername = findViewById<TextView>(R.id.tvProfileUsername)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        val btnLogout = findViewById<Button>(R.id.btnProfileLogout)
        val btnGoHome = findViewById<Button>(R.id.btnProfileGoHome)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)

        tvUsername.text = AuthStore.getCurrentUser(this) ?: "Guest"

        btnBack.setOnClickListener { finish() }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
        btnSearch.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
        btnProfile.setOnClickListener { }
        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnGoHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

        btnLogout.setOnClickListener {
            AuthStore.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        updateCartBar()
    }

    override fun onResume() {
        super.onResume()
        updateCartBar()
    }

    private fun updateCartBar() {
        val activeCart = checkoutManager.getOrCreateActiveCart()
        val count = activeCart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count == 1) "1 item" else "$count items"
        tvCartSummary.text = "View Cart • $${String.format("%.2f", activeCart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}
