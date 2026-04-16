package com.example.eflashshop.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.login.AuthStore
import com.example.eflashshop.model.SellCategory
import com.example.eflashshop.model.SellProductModel
import com.example.eflashshop.repository.SellProductRepository

class SellProductActivity : AppCompatActivity() {
    private lateinit var sellProductModel: SellProductModel
    private var selectedImageUri: Uri? = null
    private lateinit var imagePreview: ImageView
    private lateinit var imageHint: TextView

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            return@registerForActivityResult
        }

        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Keep best-effort behavior for providers that do not support persisted permissions.
        }

        selectedImageUri = uri
        imagePreview.setImageURI(uri)
        imageHint.text = "Image selected"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_product)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        sellProductModel = SellProductModel(SellProductRepository(DatabaseHelper(this)))

        val btnBack = findViewById<ImageButton>(R.id.btnBackSellProduct)
        val etProductName = findViewById<EditText>(R.id.etSellProductName)
        val etProductPrice = findViewById<EditText>(R.id.etSellProductPrice)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerSellCategory)
        val etProductDescription = findViewById<EditText>(R.id.etSellProductDescription)
        val btnUploadImage = findViewById<Button>(R.id.btnUploadProductImage)
        val btnPublish = findViewById<Button>(R.id.btnPublishProduct)
        imagePreview = findViewById(R.id.ivSellProductPreview)
        imageHint = findViewById(R.id.tvSellImageHint)

        val sellCategories = SellCategory.entries
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sellCategories.map { it.label }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        btnBack.setOnClickListener { finish() }
        btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }
        btnPublish.setOnClickListener {
            val sellerEmail = AuthStore.getCurrentEmail(this).orEmpty()
            val sellerName = AuthStore.getCurrentUser(this).orEmpty().ifBlank { sellerEmail.substringBefore("@") }
            val selectedCategory = sellCategories.getOrElse(spinnerCategory.selectedItemPosition) { SellCategory.OTHER }
            val result = sellProductModel.submitProduct(
                sellerName = sellerName,
                sellerEmail = sellerEmail,
                productName = etProductName.text.toString(),
                productPriceInput = etProductPrice.text.toString(),
                category = selectedCategory,
                description = etProductDescription.text.toString(),
                imageRef = selectedImageUri?.toString().orEmpty()
            )
            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            if (result.success && result.productId != null) {
                val intent = Intent(this, ItemDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", result.productId)
                startActivity(intent)
                finish()
            }
        }
    }
}
