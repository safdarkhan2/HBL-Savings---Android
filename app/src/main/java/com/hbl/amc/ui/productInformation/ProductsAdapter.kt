package com.hbl.amc.ui.productInformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemProductBinding
import com.hbl.amc.domain.model.Product
import kotlin.properties.Delegates

class ProductsAdapter(private val onSelectOrUnSelectProduct: (position: Int, isSelected: Boolean) -> Unit)
    : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var productsList: List<Product> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ProductsViewHolder(itemBinding)

        itemBinding.radio.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onSelectOrUnSelectProduct.invoke(holder.adapterPosition, !productsList[holder.adapterPosition].isSelected)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val product = productsList[position]
            holder.bind(product)
        }
    }

    override fun getItemCount() = productsList.size

    fun updateData(products: List<Product>) {
        productsList = products
        notifyDataSetChanged()
    }

    class ProductsViewHolder (private val itemBinding: ItemProductBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(product: Product) {
            itemBinding.radio.text = product.productName
            itemBinding.expandTextView.text = product.description
            itemBinding.radio.isChecked = product.isSelected
        }
    }
}