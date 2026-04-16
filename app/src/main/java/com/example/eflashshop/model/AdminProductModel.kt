package com.example.eflashshop.model

import com.example.eflashshop.dto.ManagedProductDTO
import com.example.eflashshop.repository.AdminProductRepository

class AdminProductModel(private val adminProductRepository: AdminProductRepository) {

    fun loadProducts(): List<ManagedProductDTO> {
        return adminProductRepository.getManagedProducts()
    }

    fun createProduct(
        name: String,
        priceInput: String,
        description: String,
        categoryName: String,
        imageRef: String
    ): String? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return "Product name is required"
        if (categoryName.trim().isEmpty()) return "Category is required"

        val price = priceInput.toDoubleOrNull()
        if (price == null || price <= 0.0) return "Price must be greater than 0"

        val insertedId = adminProductRepository.addProduct(
            name = trimmedName,
            price = price,
            description = description.trim().ifEmpty { null },
            categoryName = categoryName.trim(),
            imageRef = imageRef.trim().ifEmpty { null }
        )
        return if (insertedId > 0) null else "Unable to add product"
    }

    fun setListed(productId: Long, isListed: Boolean): Boolean {
        return adminProductRepository.setProductListed(productId, isListed)
    }

    fun deleteProduct(productId: Long): Boolean {
        return adminProductRepository.deleteProduct(productId)
    }

    fun resetDatabase(): Boolean {
        return adminProductRepository.resetDatabase()
    }
}
