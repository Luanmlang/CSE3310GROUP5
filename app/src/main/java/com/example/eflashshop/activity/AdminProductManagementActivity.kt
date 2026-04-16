package com.example.eflashshop.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etCategoryName: EditText
    private lateinit var etImageRef: EditText
    private lateinit var etProductDescription: EditText

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

        etProductName = findViewById(R.id.etManagedProductName)
        etProductPrice = findViewById(R.id.etManagedProductPrice)
        etCategoryName = findViewById(R.id.etManagedProductCategory)
        etImageRef = findViewById(R.id.etManagedProductImageRef)
        etProductDescription = findViewById(R.id.etManagedProductDescription)
        val btnAddProduct = findViewById<Button>(R.id.btnAddManagedProduct)
        val btnResetDatabase = findViewById<Button>(R.id.btnResetDatabase)
        val btnBack = findViewById<ImageButton>(R.id.btnBackAdmin)
        val recyclerView = findViewById<RecyclerView>(R.id.rvManagedProducts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adminProductAdapter

        btnBack.setOnClickListener { finish() }
        btnAddProduct.setOnClickListener { addProduct() }
        btnResetDatabase.setOnClickListener {
            val reset = adminProductModel.resetDatabase()
            Toast.makeText(
                this,
                if (reset) "Database cleared. Admin account remains active." else "Unable to reset database",
                Toast.LENGTH_SHORT
            ).show()
            if (reset) {
                clearAddProductFields()
                loadProducts()
            }
        }

        loadProducts()
    }

    private fun loadProducts() {
        val products = adminProductModel.loadProducts()
        adminProductAdapter.submitList(products)
    }

    private fun addProduct() {
        val error = adminProductModel.createProduct(
            name = etProductName.text.toString(),
            priceInput = etProductPrice.text.toString(),
            description = etProductDescription.text.toString(),
            categoryName = etCategoryName.text.toString(),
            imageRef = etImageRef.text.toString()
        )

        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
        clearAddProductFields()
        loadProducts()
    }

    private fun clearAddProductFields() {
        etProductName.text?.clear()
        etProductPrice.text?.clear()
        etCategoryName.text?.clear()
        etImageRef.text?.clear()
        etProductDescription.text?.clear()
    }
}
