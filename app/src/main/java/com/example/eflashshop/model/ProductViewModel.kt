package com.example.eflashshop.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eflashshop.dto.ProductDTO
import com.example.eflashshop.entities.Product
import com.example.eflashshop.repository.CartRepository
import com.example.eflashshop.repository.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _productDTO = MutableLiveData<ProductDTO?>()
    val productDTO: LiveData<ProductDTO?> = _productDTO

    private val _categoryName = MutableLiveData<String?>()
    val categoryName: LiveData<String?> = _categoryName

    private val _sellerName = MutableLiveData<String?>()
    val sellerName: LiveData<String?> = _sellerName

    private val _sellerEmail = MutableLiveData<String?>()
    val sellerEmail: LiveData<String?> = _sellerEmail

    private val _sellerImageRef = MutableLiveData<String?>()
    val sellerImageRef: LiveData<String?> = _sellerImageRef

    private val _cartItemCount = MutableLiveData<Int>(0)
    val cartItemCount: LiveData<Int> = _cartItemCount

    private val _showCartBar = MutableLiveData<Boolean>(false)
    val showCartBar: LiveData<Boolean> = _showCartBar

    private val _stock = MutableLiveData<Int>(0)
    val stock: LiveData<Int> = _stock

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private var cartId: Long = -1

    fun initializeCart() {
        cartId = cartRepository.getOrCreateCart()
        updateCartItemCount()
    }

    fun loadProductDetails(productId: Long) {
        val productDetail = productRepository.getProductDetailById(productId)
        if (productDetail != null) {
            _productDTO.value = productDetail
            _product.value = productDetail.product
            _stock.value = productDetail.product.stock
            _categoryName.value = productDetail.categoryName
            _sellerName.value = productDetail.sellerDTO?.name
            _sellerEmail.value = productDetail.sellerDTO?.email
            _sellerImageRef.value = productDetail.sellerDTO?.profileImageRef
        } else {
            _toastMessage.value = "Product details not found"
        }
    }

    fun deleteCartItem() {
        cartRepository.deleteAllCarts();
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
