package com.example.eflashshop.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.R
import com.example.eflashshop.entities.Product
import com.google.android.material.card.MaterialCardView

class SearchResultsAdapter(
    private val onProductClick: (Product) -> Unit,
    private val imageBinder: (ImageView, Product) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    private val products = mutableListOf<Product>()

    fun submitList(items: List<Product>) {
        products.clear()
        products.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_product, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.name
        holder.price.text = "$%.2f".format(product.price)
        imageBinder(holder.image, product)

        val openDetail = View.OnClickListener { onProductClick(product) }
        holder.card.setOnClickListener(openDetail)
        holder.chevron.setOnClickListener(openDetail)
    }

    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.productCard)
        val image: ImageView = itemView.findViewById(R.id.ivProductImage)
        val title: TextView = itemView.findViewById(R.id.tvProductTitle)
        val price: TextView = itemView.findViewById(R.id.tvProductPrice)
        val chevron: ImageView = itemView.findViewById(R.id.ivChevron)
    }
}
