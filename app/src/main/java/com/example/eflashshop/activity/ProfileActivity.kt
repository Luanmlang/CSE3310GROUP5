package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.login.AuthStore

class ProfileActivity : AppCompatActivity() {
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvProfileEmail: TextView
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
        val username = AuthStore.getCurrentUser(this) ?: "Guest"
        val email = AuthStore.getCurrentEmail(this) ?: "guest@shopmail.com"

        tvProfileUsername = findViewById(R.id.tvProfileUsername)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        val btnLogoutProfile = findViewById<Button>(R.id.btnLogoutProfile)
        val cardAddresses = findViewById<View>(R.id.cardAddresses)
        val cardAdminProducts = findViewById<View>(R.id.cardAdminProducts)
        val cardPaymentMethods = findViewById<View>(R.id.cardPaymentMethods)
        val cardHelpCenter = findViewById<View>(R.id.cardHelpCenter)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnAddProduct = findViewById<ImageButton>(R.id.btnAddProduct)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)

        loadProfileFields(username, email)

        val isAdmin = AuthStore.isAdmin(this)
        cardAdminProducts.visibility = if (isAdmin) View.VISIBLE else View.GONE
        cardAdminProducts.setOnClickListener {
            startActivity(Intent(this, AdminProductManagementActivity::class.java))
        }
        cardAddresses.setOnClickListener {
            Toast.makeText(this, "Address settings coming soon", Toast.LENGTH_SHORT).show()
        }
        cardPaymentMethods.setOnClickListener {
            Toast.makeText(this, "Payment methods coming soon", Toast.LENGTH_SHORT).show()
        }
        cardHelpCenter.setOnClickListener {
            Toast.makeText(this, "Help Center coming soon", Toast.LENGTH_SHORT).show()
        }
        btnLogoutProfile.setOnClickListener {
            AuthStore.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        btnBack.setOnClickListener { finish() }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchResultsActivity::class.java))
        }
        btnProfile.setOnClickListener { }
        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SellProductActivity::class.java))
        }
        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        updateCartBar()
    }

    override fun onResume() {
        super.onResume()
        updateCartBar()
    }

    private fun loadProfileFields(username: String, loginEmail: String) {
        val prefs = getSharedPreferences(PROFILE_PREFS_NAME, MODE_PRIVATE)
        val defaultEmail = loginEmail
        val fullName = prefs.getString(profileKey(KEY_FULL_NAME, username), username).orEmpty()
        val email = prefs.getString(profileKey(KEY_EMAIL, username), defaultEmail).orEmpty()
        tvProfileUsername.text = fullName
        tvProfileEmail.text = email
    }

    private fun profileKey(field: String, username: String): String {
        return "${field}_$username"
    }

    private fun updateCartBar() {
        val activeCart = checkoutManager.getOrCreateActiveCart()
        val count = activeCart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count > 99) "99+" else count.toString()
        tvCartSummary.text = "Cart • $${String.format("%.2f", activeCart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    companion object {
        private const val PROFILE_PREFS_NAME = "profile_settings"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
    }
}
