package com.example.eflashshop.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.model.ProductViewModel
import com.example.eflashshop.repository.ProductRepository
import com.example.eflashshop.repository.CartRepository

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var tvProductName: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var tvProductCategory: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var btnBuyNow: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnProfile: ImageButton
    private lateinit var btnCart: ImageButton
    private lateinit var tvCartBadge: TextView
    private lateinit var btnIncrement: Button
    private lateinit var btnDecrement: Button
    private lateinit var etQuantity: EditText
    private lateinit var productViewModel: ProductViewModel
    private var productId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val dbHelper = DatabaseHelper(this)
        val productRepository = ProductRepository(dbHelper)
        val cartRepository = CartRepository(dbHelper)

        productViewModel = ProductViewModel(productRepository, cartRepository)
        productViewModel.deleteCartItem();

        initViews()
        observeViewModel()

        productId = intent.getLongExtra("PRODUCT_ID", -1)

        productViewModel.loadProductDetails(productId)

        setupClickListeners()
    }

    private fun initViews() {
        tvProductName = findViewById(R.id.tvProductName)
        tvProductPrice = findViewById(R.id.tvProductPrice)
        tvProductDescription = findViewById(R.id.tvProductDescription)
        tvProductCategory = findViewById(R.id.tvProductCategory)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnBack = findViewById(R.id.btnBack)
        btnHome = findViewById(R.id.btnHome)
        btnSearch = findViewById(R.id.btnSearch)
        btnProfile = findViewById(R.id.btnProfile)
        btnCart = findViewById(R.id.btnCart)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        btnIncrement = findViewById(R.id.btnIncrement)
        btnDecrement = findViewById(R.id.btnDecrement)
        etQuantity = findViewById(R.id.etQuantity)
    }

    private fun observeViewModel() {
        productViewModel.product.observe(this) { product ->
            if (product != null) {
                tvProductName.text = product.name
                tvProductPrice.text = "$${product.price}"
                tvProductDescription.text = product.description
            }
        }

        productViewModel.categoryName.observe(this) { categoryName ->
            if (categoryName != null) {
                tvProductCategory.text = "Category: $categoryName"
            }
        }

        productViewModel.cartItemCount.observe(this) { count ->
            tvCartBadge.text = count.toString()
            tvCartBadge.visibility = if (count > 0) android.view.View.VISIBLE else android.view.View.GONE
        }

        productViewModel.showCartBar.observe(this) { show ->
            val cartBar = findViewById<FrameLayout>(R.id.cartBar)
            cartBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun setupClickListeners() {
        btnAddToCart.setOnClickListener {
            productViewModel.initializeCart()
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 1
            productViewModel.addToCart(productId, quantity)
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnIncrement.setOnClickListener {
            val currentQuantity = etQuantity.text.toString().toIntOrNull() ?: 1
            etQuantity.setText((currentQuantity + 1).toString())
        }

        btnDecrement.setOnClickListener {
            val currentQuantity = etQuantity.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                etQuantity.setText((currentQuantity - 1).toString())
            }
        }
    }
}