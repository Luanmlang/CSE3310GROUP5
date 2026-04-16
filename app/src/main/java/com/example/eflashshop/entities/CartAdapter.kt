package com.example.eflashshop.entities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eflashshop.R

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onRemoveClick: (Long) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.itemName)
        val priceText: TextView = view.findViewById(R.id.itemPrice)
        val quantityText: TextView = view.findViewById(R.id.itemQuantity)
        val removeButton: Button = view.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.nameText.text = item.productName
        holder.priceText.text = "$${item.price}"
        holder.quantityText.text = "Qty: ${item.quantity}"
        holder.removeButton.setOnClickListener {
            onRemoveClick(item.id)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}