package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.DatabaseHelper
import com.example.eflashshop.R
import com.example.eflashshop.checkout.CheckoutManager
import com.example.eflashshop.entities.Product
import com.example.eflashshop.login.AuthStore
import com.example.eflashshop.model.CatalogModel
import com.example.eflashshop.model.ProductAssetModel
import com.example.eflashshop.repository.ProductRepository
import com.google.android.material.card.MaterialCardView

class HomePageActivity : AppCompatActivity() {
    private data class SupportCardViews(
        val card: MaterialCardView,
        val image: ImageView,
        val name: TextView,
        val meta: TextView,
        val price: TextView,
        val arrow: ImageView
    )

    private lateinit var catalogModel: CatalogModel
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var heroProductCard: MaterialCardView
    private lateinit var heroProductImage: ImageView
    private lateinit var heroBadge: TextView
    private lateinit var heroProductName: TextView
    private lateinit var heroProductPrice: TextView
    private lateinit var heroProductArrow: ImageView
    private lateinit var supportCards: List<SupportCardViews>
    private lateinit var cartBar: View
    private lateinit var tvCartBadge: TextView
    private lateinit var tvCartSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val dbHelper = DatabaseHelper(this)
        catalogModel = CatalogModel(ProductRepository(dbHelper))
        checkoutManager = CheckoutManager(dbHelper)
        heroProductCard = findViewById(R.id.heroProductCard)
        heroProductImage = findViewById(R.id.heroProductImage)
        heroBadge = findViewById(R.id.heroBadge)
        heroProductName = findViewById(R.id.heroProductName)
        heroProductPrice = findViewById(R.id.heroProductPrice)
        heroProductArrow = findViewById(R.id.heroProductArrow)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnAddProduct = findViewById<ImageButton>(R.id.btnAddProduct)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        cartBar = findViewById(R.id.cartBar)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        tvCartSummary = findViewById(R.id.tvCartSummary)

        supportCards = listOf(
            createSupportCardViews(R.id.supportCard1),
            createSupportCardViews(R.id.supportCard2),
            createSupportCardViews(R.id.supportCard3)
        )

        btnHome.setOnClickListener {
            loadHomeProducts()
        }
        btnSearch.setOnClickListener {
            openSearchResults()
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

        loadHomeProducts()
        updateCartBar()
    }

    override fun onResume() {
        super.onResume()
        updateCartBar()
    }

    private fun createSupportCardViews(cardId: Int): SupportCardViews {
        val card = findViewById<MaterialCardView>(cardId)
        return SupportCardViews(
            card = card,
            image = card.findViewById(R.id.supportProductImage),
            name = card.findViewById(R.id.supportProductName),
            meta = card.findViewById(R.id.supportProductMeta),
            price = card.findViewById(R.id.supportProductPrice),
            arrow = card.findViewById(R.id.supportProductArrow)
        )
    }

    private fun loadHomeProducts() {
        val products = catalogModel.loadHomeProducts()
        if (products.isEmpty()) {
            heroProductCard.visibility = View.GONE
            supportCards.forEach { it.card.visibility = View.GONE }
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
            return
        }

        bindHeroProduct(products.first())

        val supportProducts = products.drop(1)
        supportCards.forEachIndexed { index, supportViews ->
            if (index < supportProducts.size) {
                bindSupportProduct(supportViews, supportProducts[index], index)
                supportViews.card.visibility = View.VISIBLE
            } else {
                supportViews.card.visibility = View.GONE
            }
        }
    }

    private fun bindHeroProduct(product: Product) {
        heroProductCard.visibility = View.VISIBLE
        heroProductName.text = product.name
        heroProductPrice.text = "$%.2f".format(product.price)
        heroBadge.text = "Top Pick • ${catalogModel.categoryLabelFor(product)}"
        ProductAssetModel.bindProductImage(heroProductImage, product)
        val openDetail = View.OnClickListener { openProductDetails(product) }
        heroProductCard.setOnClickListener(openDetail)
        heroProductArrow.setOnClickListener(openDetail)
    }

    private fun bindSupportProduct(views: SupportCardViews, product: Product, position: Int) {
        views.name.text = product.name
        views.price.text = "$%.2f".format(product.price)
        views.meta.text = "★ %.1f • %s".format(
            catalogModel.ratingFor(product, position),
            catalogModel.categoryLabelFor(product)
        )
        ProductAssetModel.bindProductImage(views.image, product)
        val openDetail = View.OnClickListener { openProductDetails(product) }
        views.card.setOnClickListener(openDetail)
        views.arrow.setOnClickListener(openDetail)
    }

    private fun openProductDetails(product: Product) {
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        startActivity(intent)
    }

    private fun openSearchResults(query: String = "") {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("SEARCH_QUERY", query)
        startActivity(intent)
    }

    private fun updateCartBar() {
        val activeCart = checkoutManager.getOrCreateActiveCart()
        val count = activeCart.items.sumOf { it.quantity }
        tvCartBadge.text = if (count > 99) "99+" else count.toString()
        tvCartSummary.text = "Cart • $${String.format("%.2f", activeCart.getTotal())}"
        cartBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}
