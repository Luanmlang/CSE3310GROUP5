package com.example.eflashshop.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eflashshop.entities.Product
import com.example.eflashshop.repository.CartRepository
import com.example.eflashshop.repository.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _categoryName = MutableLiveData<String?>()
    val categoryName: LiveData<String?> = _categoryName

    private val _cartItemCount = MutableLiveData<Int>(0)
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _showCartBar = MutableLiveData<Boolean>(false)
    val showCartBar: LiveData<Boolean> = _showCartBar

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private var cartId: Long = -1

    fun initializeCart() {
        cartId = cartRepository.getOrCreateCart()
        updateCartItemCount()
    }

    fun loadProductDetails(productId: Long) {
        val product = productRepository.getProductById(productId)
        if (product != null) {
            _product.value = product
            loadCategoryName(product.categoryId)
        } else {
            _toastMessage.value = "Product details not found"
        }
    }

    fun deleteCartItem() {
        cartRepository.deleteAllCarts();
    }

    private fun loadCategoryName(categoryId: Long) {
        val categoryName = productRepository.getCategoryNameById(categoryId)
        _categoryName.value = categoryName
    }

    fun addToCart(productId: Long, quantity: Int) {
        cartRepository.addItemToCart(cartId, productId, quantity)
        updateCartItemCount()
    }

    private fun updateCartItemCount() {
        val itemCount = cartRepository.getCartItemCount(cartId)
        _cartItemCount.value = itemCount
        _showCartBar.value = itemCount > 0
    }

    fun getCartId(): Long = cartId
}
