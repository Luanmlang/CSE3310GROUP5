package com.example.eflashshop.dto

import com.example.eflashshop.entities.Product
import com.example.eflashshop.dto.SellerDTO

data class ProductDTO(
    val product: Product,
    val categoryName: String?,
    val sellerDTO: SellerDTO?
)