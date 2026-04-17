package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.login.AuthStore
import com.example.eflashshop.model.SellerProductModel
import com.example.eflashshop.repository.SellerProductRepository

class SellerProductManagementActivity : AppCompatActivity() {
    private lateinit var sellerProductModel: SellerProductModel
    private lateinit var sellerProductAdapter: SellerProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_product_management)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val sellerEmail = AuthStore.getCurrentEmail(this) ?: run {
            finish()
            return
        }

        val dbHelper = DatabaseHelper(this)
        sellerProductModel = SellerProductModel(SellerProductRepository(dbHelper), sellerEmail)
        sellerProductAdapter = SellerProductAdapter(
            onListingChanged = { product, isListed ->
                val updated = sellerProductModel.setListed(product.id, isListed)
                if (!updated) {
                    Toast.makeText(this, "Unable to update listing state", Toast.LENGTH_SHORT).show()
                }
                loadProducts()
            },
            onDelete = { product ->
                val deleted = sellerProductModel.deleteProduct(product.id)
                Toast.makeText(
                    this,
                    if (deleted) "Product deleted" else "Unable to delete product",
                    Toast.LENGTH_SHORT
                ).show()
                if (deleted) {
                    loadProducts()
                }
            }
        )

        val btnAddProduct = findViewById<Button>(R.id.btnAddSellerProduct)
        val btnBack = findViewById<ImageButton>(R.id.btnBackSeller)
        val recyclerView = findViewById<RecyclerView>(R.id.rvSellerProducts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = sellerProductAdapter

        btnBack.setOnClickListener { finish() }
        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SellProductActivity::class.java))
        }

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val products = sellerProductModel.loadProducts()
        sellerProductAdapter.submitList(products)
    }
}
