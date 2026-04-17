package com.example.eflashshop.model

import com.example.eflashshop.dto.ManagedProductDTO
import com.example.eflashshop.repository.SellerProductRepository

class SellerProductModel(
    private val sellerProductRepository: SellerProductRepository,
    private val sellerEmail: String
) {

    fun loadProducts(): List<ManagedProductDTO> {
        return sellerProductRepository.getProductsForSeller(sellerEmail)
    }

    fun setListed(productId: Long, isListed: Boolean): Boolean {
        return sellerProductRepository.setProductListed(productId, sellerEmail, isListed)
    }

    fun deleteProduct(productId: Long): Boolean {
        return sellerProductRepository.deleteProduct(productId, sellerEmail)
    }
}
