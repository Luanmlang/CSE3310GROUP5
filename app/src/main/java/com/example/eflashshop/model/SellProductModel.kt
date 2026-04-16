package com.example.eflashshop.model

import com.example.eflashshop.repository.SellProductRepository

data class SellProductResult(
    val success: Boolean,
    val message: String,
    val productId: Long? = null
)

class SellProductModel(private val sellProductRepository: SellProductRepository) {

    fun submitProduct(
        sellerName: String,
        sellerEmail: String,
        productName: String,
        productPriceInput: String,
        category: SellCategory,
        description: String,
        imageRef: String
    ): SellProductResult {
        val trimmedProductName = productName.trim()
        if (trimmedProductName.isEmpty()) {
            return SellProductResult(false, "Product name is required")
        }

        val price = productPriceInput.trim().toDoubleOrNull()
        if (price == null || price <= 0.0) {
            return SellProductResult(false, "Enter a valid price")
        }

        val normalizedEmail = sellerEmail.trim().lowercase()
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            return SellProductResult(false, "Login session is invalid. Please log in again.")
        }

        val insertedId = sellProductRepository.addProductForSeller(
            sellerName = sellerName,
            sellerEmail = normalizedEmail,
            productName = trimmedProductName,
            productPrice = price,
            categoryName = category.label,
            description = description.trim().ifEmpty { null },
            imageRef = imageRef.trim().ifEmpty { null }
        )

        return if (insertedId > 0) {
            SellProductResult(true, "Product listed successfully", insertedId)
        } else {
            SellProductResult(false, "Unable to list product")
        }
    }
}
