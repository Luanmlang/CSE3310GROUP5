package com.example.eflashshop.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.entities.Product
import com.example.eflashshop.login.AuthStore
import com.example.eflashshop.model.CatalogModel
import com.example.eflashshop.model.ProductAssetModel
import com.example.eflashshop.repository.ProductRepository
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class SearchResultsActivity : AppCompatActivity() {
    private enum class FilterMode(val label: String) {
        ALL("All"),
        BUDGET("Under $50"),
        PREMIUM("$50+")
    }

    private enum class SortMode(val label: String) {
        FEATURED("Featured"),
        PRICE_LOW_HIGH("Price ↑"),
        PRICE_HIGH_LOW("Price ↓"),
        RATING_HIGH_LOW("Rating")
    }

    private lateinit var catalogModel: CatalogModel
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var searchAdapter: SearchResultsAdapter
    private lateinit var etSearchQuery: EditText
    private lateinit var tvFilterLabel: TextView
    private lateinit var tvSortLabel: TextView
    private lateinit var tvSearchEmpty: TextView
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView
    private var currentFilter = FilterMode.ALL
    private var currentSort = SortMode.FEATURED
    private var currentQuery: String = ""
    private var allProducts = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val dbHelper = DatabaseHelper(this)
        catalogModel = CatalogModel(ProductRepository(dbHelper))
        checkoutManager = CheckoutManager(dbHelper)

        etSearchQuery = findViewById(R.id.etSearchQuery)
        tvFilterLabel = findViewById(R.id.tvFilterLabel)
        tvSortLabel = findViewById(R.id.tvSortLabel)
        tvSearchEmpty = findViewById(R.id.tvSearchEmpty)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)
        val rvSearchResults = findViewById<RecyclerView>(R.id.rvSearchResults)
        val btnSearchApply = findViewById<ImageButton>(R.id.btnSearchApply)
        val filterChip = findViewById<MaterialCardView>(R.id.filterChip)
        val sortChip = findViewById<MaterialCardView>(R.id.sortChip)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnAddProduct = findViewById<ImageButton>(R.id.btnAddProduct)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)

        searchAdapter = SearchResultsAdapter(
            onProductClick = { product ->
                openProductDetails(product)
            },
            imageBinder = { imageView, product ->
                ProductAssetModel.bindProductImage(imageView, product)
            },
            ratingProvider = { product, position ->
                catalogModel.ratingFor(product, position)
            }
        )

        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        rvSearchResults.adapter = searchAdapter
        rvSearchResults.addItemDecoration(
            GridSpacingDecoration(
                spanCount = 2,
                spacingPx = (12 * resources.displayMetrics.density).roundToInt()
            )
        )

        currentQuery = intent.getStringExtra("SEARCH_QUERY").orEmpty().trim()
        etSearchQuery.setText(currentQuery)
        etSearchQuery.setSelection(etSearchQuery.text.length)

        btnSearchApply.setOnClickListener {
            performSearch()
        }
        btnSearch.setOnClickListener {
            performSearch()
        }
        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
        filterChip.setOnClickListener {
            showFilterDialog()
        }
        sortChip.setOnClickListener {
            showSortDialog()
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
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

        loadProducts()
        updateCartBar()
    }

    override fun onResume() {
        super.onResume()
        updateCartBar()
    }

    private fun performSearch() {
        currentQuery = etSearchQuery.text.toString().trim()
        loadProducts()
    }

    private fun loadProducts() {
        allProducts = catalogModel.searchProducts(currentQuery)
        renderProducts()
    }

    private fun renderProducts() {
        var products = allProducts

        products = when (currentFilter) {
            FilterMode.ALL -> products
            FilterMode.BUDGET -> products.filter { it.price < 50.0 }
            FilterMode.PREMIUM -> products.filter { it.price >= 50.0 }
        }

        products = when (currentSort) {
            SortMode.FEATURED -> products.sortedBy { it.id }
            SortMode.PRICE_LOW_HIGH -> products.sortedBy { it.price }
            SortMode.PRICE_HIGH_LOW -> products.sortedByDescending { it.price }
            SortMode.RATING_HIGH_LOW -> products.sortedByDescending { catalogModel.ratingFor(it, it.id.toInt()) }
        }

        tvFilterLabel.text = currentFilter.label
        tvSortLabel.text = currentSort.label
        tvSearchEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        searchAdapter.submitList(products)
    }

    private fun showFilterDialog() {
        val modes = FilterMode.entries.toTypedArray()
        val labels = modes.map { it.label }.toTypedArray()
        var selectedIndex = modes.indexOf(currentFilter)

        AlertDialog.Builder(this)
            .setTitle("Filter")
            .setSingleChoiceItems(labels, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Apply") { _, _ ->
                currentFilter = modes[selectedIndex]
                renderProducts()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSortDialog() {
        val modes = SortMode.entries.toTypedArray()
        val labels = modes.map { it.label }.toTypedArray()
        var selectedIndex = modes.indexOf(currentSort)

        AlertDialog.Builder(this)
            .setTitle("Sort")
            .setSingleChoiceItems(labels, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Apply") { _, _ ->
                currentSort = modes[selectedIndex]
                renderProducts()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openProductDetails(product: Product) {
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        startActivity(intent)
    }

    private fun updateCartBar() {
        val activeCart = checkoutManager.getOrCreateActiveCart()
        val count = activeCart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count > 99) "99+" else count.toString()
        tvCartSummary.text = "Cart • $${String.format("%.2f", activeCart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    private class GridSpacingDecoration(
        private val spanCount: Int,
        private val spacingPx: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            outRect.left = spacingPx - column * spacingPx / spanCount
            outRect.right = (column + 1) * spacingPx / spanCount
            if (position < spanCount) {
                outRect.top = spacingPx
            }
            outRect.bottom = spacingPx
        }
    }
}
