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
import com.example.eflashshop.model.AdminProductModel
import com.example.eflashshop.repository.AdminProductRepository

class AdminProductManagementActivity : AppCompatActivity() {
    private lateinit var adminProductModel: AdminProductModel
    private lateinit var adminProductAdapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product_management)

        if (!AuthStore.isAdmin(this)) {
            Toast.makeText(this, "Admin access only", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val dbHelper = DatabaseHelper(this)
        adminProductModel = AdminProductModel(AdminProductRepository(dbHelper))
        adminProductAdapter = AdminProductAdapter(
            onListingChanged = { product, isListed ->
                val updated = adminProductModel.setListed(product.id, isListed)
                if (!updated) {
                    Toast.makeText(this, "Unable to update listing state", Toast.LENGTH_SHORT).show()
                }
                loadProducts()
            },
            onDelete = { product ->
                val deleted = adminProductModel.deleteProduct(product.id)
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

        val btnAddProduct = findViewById<Button>(R.id.btnAddManagedProduct)
        val btnResetDatabase = findViewById<Button>(R.id.btnResetDatabase)
        val btnBack = findViewById<ImageButton>(R.id.btnBackAdmin)
        val recyclerView = findViewById<RecyclerView>(R.id.rvManagedProducts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adminProductAdapter

        btnBack.setOnClickListener { finish() }
        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SellProductActivity::class.java))
        }
        btnResetDatabase.setOnClickListener {
            val reset = adminProductModel.resetDatabase()
            Toast.makeText(
                this,
                if (reset) "Database cleared. Admin account remains active." else "Unable to reset database",
                Toast.LENGTH_SHORT
            ).show()
            if (reset) {
                loadProducts()
            }
        }

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val products = adminProductModel.loadProducts()
        adminProductAdapter.submitList(products)
    }
}
