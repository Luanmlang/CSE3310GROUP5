package com.example.eflashshop.model

import android.net.Uri
import android.widget.ImageView
import com.example.eflashshop.R
import com.example.eflashshop.entities.Product

object ProductAssetModel {
    fun resolveProductImage(product: Product): Int {
        return resolveProductImage(product.imageRef, product.name)
    }

    fun bindProductImage(imageView: ImageView, product: Product) {
        bindProductImage(imageView, product.imageRef, product.name)
    }

    fun bindProductImage(imageView: ImageView, imageRef: String?, fallbackName: String) {
        val normalizedRef = imageRef.orEmpty().trim()
        if (isUriRef(normalizedRef)) {
            try {
                imageView.setImageURI(Uri.parse(normalizedRef))
                if (imageView.drawable != null) {
                    return
                }
            } catch (_: Exception) {
                // Fallback to drawable resolution below.
            }
        }

        imageView.setImageResource(resolveProductImage(imageRef, fallbackName))
    }

    fun resolveProductImage(imageRef: String?, fallbackName: String): Int {
        val normalizedRef = imageRef.orEmpty().trim().lowercase()
        if (normalizedRef.isNotEmpty()) {
            return when (normalizedRef) {
                "headphones_playstore", "headphones" -> R.drawable.headphones_playstore
                "mouse", "gaming_mouse" -> R.drawable.mouse
                "watch", "smart_watch" -> R.drawable.watch
                "speaker" -> R.drawable.speaker
                "stand" -> R.drawable.stand
                else -> R.drawable.image_placeholder
            }
        }

        return when {
            fallbackName.contains("headphones", ignoreCase = true) -> R.drawable.headphones_playstore
            fallbackName.contains("mouse", ignoreCase = true) -> R.drawable.mouse
            fallbackName.contains("watch", ignoreCase = true) -> R.drawable.watch
            fallbackName.contains("speaker", ignoreCase = true) -> R.drawable.speaker
            else -> R.drawable.image_placeholder
        }
    }

    fun resolveUserAvatar(imageRef: String?): Int {
        return when (imageRef.orEmpty().trim().lowercase()) {
            "ic_profile", "profile", "avatar", "seller_avatar", "buyer_avatar" -> R.drawable.ic_profile
            else -> R.drawable.ic_profile
        }
    }

    private fun isUriRef(imageRef: String): Boolean {
        return imageRef.startsWith("content://") ||
            imageRef.startsWith("file://") ||
            imageRef.startsWith("android.resource://")
    }
}
