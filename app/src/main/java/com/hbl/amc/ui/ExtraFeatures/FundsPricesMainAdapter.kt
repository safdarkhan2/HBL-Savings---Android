package com.hbl.amc.ui.ExtraFeatures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemFundsPricesMainBinding
import kotlin.properties.Delegates

class FundsPricesMainAdapter :
    RecyclerView.Adapter<FundsPricesMainAdapter.ItemViewHolder>() {

    private var itemList: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FundsPricesMainAdapter.ItemViewHolder {
        val itemBinding =
            ItemFundsPricesMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = FundsPricesMainAdapter.ItemViewHolder(itemBinding)

        return holder
    }

    override fun onBindViewHolder(
        holder: FundsPricesMainAdapter.ItemViewHolder,
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

    class ItemViewHolder(private val itemBinding: ItemFundsPricesMainBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            itemBinding.fundPricesNameTv.text = "Fixed Income Funds (20%)"
            itemBinding.fundPricesDropdownBtn.setOnClickListener {
                if (!itemBinding.fundPricesSubLayout.isVisible) {
                    itemBinding.fundPricesSubLayout.visibility = View.VISIBLE
                    itemBinding.fundPricesDropdownBtn.rotation = 0f
                } else {
                    itemBinding.fundPricesSubLayout.visibility = View.GONE
                    itemBinding.fundPricesDropdownBtn.rotation = 270f
                }
            }
        }
    }
}