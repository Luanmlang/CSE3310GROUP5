package com.example.eflashshop.model

import com.example.eflashshop.entities.Product
import com.example.eflashshop.repository.ProductRepository

class CatalogModel(private val productRepository: ProductRepository) {
    fun loadHomeProducts(limit: Int = 5): List<Product> {
        return productRepository.getFeaturedProducts(limit)
    }

    fun searchProducts(query: String): List<Product> {
        return productRepository.searchProducts(query)
    }

    fun categoryLabelFor(product: Product): String {
        return productRepository.getCategoryNameById(product.categoryId) ?: "Lifestyle"
    }

    fun ratingFor(product: Product, position: Int): Double {
        val seed = ((product.id + position) % 10).toDouble()
        return 4.0 + (seed * 0.09)
    }
}
