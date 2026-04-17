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

class SellerProductAdapter(
    private val onListingChanged: (ManagedProductDTO, Boolean) -> Unit,
    private val onDelete: (ManagedProductDTO) -> Unit
) : RecyclerView.Adapter<SellerProductAdapter.SellerProductViewHolder>() {

    private val products = mutableListOf<ManagedProductDTO>()

    fun submitList(items: List<ManagedProductDTO>) {
        products.clear()
        products.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seller_product, parent, false)
        return SellerProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: SellerProductViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.name
        holder.price.text = "$%.2f".format(product.price)
        holder.category.text = "Category: ${product.categoryName}"
        holder.stock.text = "Stock: ${product.stock}"
        holder.status.text = if (product.isListed) "Status: Listed" else "Status: Unlisted"
        ProductAssetModel.bindProductImage(holder.image, product.imageRef, product.name)

        holder.switchListed.setOnCheckedChangeListener(null)
        holder.switchListed.isChecked = product.isListed
        holder.switchListed.setOnCheckedChangeListener { _, isChecked ->
            onListingChanged(product, isChecked)
        }

        holder.deleteButton.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount(): Int = products.size

    class SellerProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.ivSellerProductImage)
        val title: TextView = itemView.findViewById(R.id.tvSellerProductTitle)
        val price: TextView = itemView.findViewById(R.id.tvSellerProductPrice)
        val category: TextView = itemView.findViewById(R.id.tvSellerProductCategory)
        val stock: TextView = itemView.findViewById(R.id.tvSellerProductStock)
        val status: TextView = itemView.findViewById(R.id.tvSellerProductStatus)
        val switchListed: SwitchCompat = itemView.findViewById(R.id.switchSellerProductListed)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteSellerProduct)
    }
}
