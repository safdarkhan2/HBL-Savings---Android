package com.hbl.amc.ui.self_service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemAssetClasswiseFundBinding
import kotlin.properties.Delegates

class AssetClassWisePortfolioFundAdapter :
    RecyclerView.Adapter<AssetClassWisePortfolioFundAdapter.ItemViewHolder>() {

    private var itemList: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val itemBinding =
            ItemAssetClasswiseFundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ItemViewHolder(itemBinding)

        return holder
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        if (position != RecyclerView.NO_POSITION) {
            val product = itemList[position]
            holder.bind()
        }
    }

    override fun getItemCount() = itemList.size

    fun updateData(items: List<String>) {
        itemList = items
        notifyDataSetChanged()
    }

    class ItemViewHolder(private val itemBinding: ItemAssetClasswiseFundBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            itemBinding.fundNameTv.text = "Fixed Income Funds (20%)"
            itemBinding.fundDetailTv.text = "HBL Islamic Income Fund (90%) \n HBL Income Fund (10%)"
        }
    }


}
