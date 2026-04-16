package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.activity.CartActivity
import com.example.eflashshop.model.ProductAssetModel
import com.example.eflashshop.model.ProductViewModel
import com.example.eflashshop.repository.ProductRepository
import com.example.eflashshop.repository.CartRepository

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var ivProductImage: ImageView
    private lateinit var ivSellerAvatar: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var tvSellerName: TextView
    private lateinit var tvSellerEmail: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var tvProductCategory: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var btnBuyNow: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnProfile: ImageButton
    private lateinit var btnAddProduct: ImageButton
    private lateinit var cartBar: View
    private lateinit var btnCart: ImageButton
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView
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

        initViews()
        observeViewModel()

        productId = intent.getLongExtra("PRODUCT_ID", -1)

        productViewModel.loadProductDetails(productId)

        setupClickListeners()
    }

    private fun initViews() {
        ivProductImage = findViewById(R.id.ivProductImage)
        ivSellerAvatar = findViewById(R.id.ivSellerAvatar)
        tvProductName = findViewById(R.id.tvProductName)
        tvSellerName = findViewById(R.id.tvSellerName)
        tvSellerEmail = findViewById(R.id.tvSellerEmail)
        tvProductPrice = findViewById(R.id.tvProductPrice)
        tvProductDescription = findViewById(R.id.tvProductDescription)
        tvProductCategory = findViewById(R.id.tvProductCategory)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnBack = findViewById(R.id.btnBack)
        btnHome = findViewById(R.id.btnHome)
        btnSearch = findViewById(R.id.btnSearch)
        btnProfile = findViewById(R.id.btnProfile)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        cartBar = findViewById(R.id.cartBar)
        btnCart = findViewById(R.id.btnCart)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)
        btnIncrement = findViewById(R.id.btnIncrement)
        btnDecrement = findViewById(R.id.btnDecrement)
        etQuantity = findViewById(R.id.etQuantity)
    }

    private fun observeViewModel() {
        productViewModel.product.observe(this) { product ->
            if (product != null) {
                ProductAssetModel.bindProductImage(ivProductImage, product)
                tvProductName.text = product.name
                tvProductPrice.text = "$${product.price}"
                tvProductDescription.text = product.description.orEmpty()
            }
        }

        productViewModel.categoryName.observe(this) { categoryName ->
            if (categoryName != null) {
                tvProductCategory.text = "Category: $categoryName"
            }
        }

        productViewModel.sellerName.observe(this) { sellerName ->
            tvSellerName.text = if (sellerName.isNullOrBlank()) {
                "Sold by Marketplace Seller"
            } else {
                "Sold by $sellerName"
            }
        }

        productViewModel.sellerEmail.observe(this) { sellerEmail ->
            tvSellerEmail.text = if (sellerEmail.isNullOrBlank()) {
                "seller@shop.com"
            } else {
                sellerEmail
            }
        }

        productViewModel.sellerImageRef.observe(this) { imageRef ->
            ivSellerAvatar.setImageResource(ProductAssetModel.resolveUserAvatar(imageRef))
        }

        productViewModel.cartItemCount.observe(this) { count ->
            tvCartBadge.text = if (count > 99) "99+" else count.toString()
            tvCartSummary.text = "Cart"
        }

        productViewModel.showCartBar.observe(this) { show ->
            cartBar.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        btnAddToCart.setOnClickListener {
            productViewModel.initializeCart()
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 1
            productViewModel.addToCart(productId, quantity)
        }

        btnBuyNow.setOnClickListener {
            productViewModel.initializeCart()
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 1
            productViewModel.addToCart(productId, quantity)
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchResultsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SellProductActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
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
