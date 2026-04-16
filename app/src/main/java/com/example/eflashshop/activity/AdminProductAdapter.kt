package com.example.eflashshop.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.R
import com.example.eflashshop.dto.ManagedProductDTO
import com.example.eflashshop.model.ProductAssetModel

class AdminProductAdapter(
    private val onListingChanged: (ManagedProductDTO, Boolean) -> Unit,
    private val onDelete: (ManagedProductDTO) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    private val products = mutableListOf<ManagedProductDTO>()

    fun submitList(items: List<ManagedProductDTO>) {
        products.clear()
        products.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.name
        holder.price.text = "$%.2f".format(product.price)
        holder.category.text = "Category: ${product.categoryName}"
        holder.seller.text = "Seller: ${product.sellerName}"
        holder.status.text = if (product.isListed) "Status: Listed" else "Status: Disabled"
        ProductAssetModel.bindProductImage(holder.image, product.imageRef, product.name)

        holder.switchListed.setOnCheckedChangeListener(null)
        holder.switchListed.isChecked = product.isListed
        holder.switchListed.setOnCheckedChangeListener { _, isChecked ->
            onListingChanged(product, isChecked)
        }

        holder.deleteButton.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount(): Int = products.size

    class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.ivManagedProductImage)
        val title: TextView = itemView.findViewById(R.id.tvManagedProductTitle)
        val price: TextView = itemView.findViewById(R.id.tvManagedProductPrice)
        val category: TextView = itemView.findViewById(R.id.tvManagedProductCategory)
        val seller: TextView = itemView.findViewById(R.id.tvManagedProductSeller)
        val status: TextView = itemView.findViewById(R.id.tvManagedProductStatus)
        val switchListed: SwitchCompat = itemView.findViewById(R.id.switchManagedProductListed)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteManagedProduct)
    }
}
