package com.hbl.amc.ui.ExtraFeatures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemFaqMutualfundsMainBinding
import kotlin.properties.Delegates

class FaqMutualFundsMainItemAdapter :
    RecyclerView.Adapter<FaqMutualFundsMainItemAdapter.ItemViewHolder>() {

    private var itemList: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FaqMutualFundsMainItemAdapter.ItemViewHolder {
        val itemBinding =
            ItemFaqMutualfundsMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = FaqMutualFundsMainItemAdapter.ItemViewHolder(itemBinding)

        return holder
    }

    override fun onBindViewHolder(
        holder: FaqMutualFundsMainItemAdapter.ItemViewHolder,
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

    class ItemViewHolder(private val itemBinding: ItemFaqMutualfundsMainBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var faqMutualFundsSubAdapter : FaqMutualFundsSubItemAdapter

        fun bind() {
            faqMutualFundsSubAdapter = FaqMutualFundsSubItemAdapter(listOf(
                "HBL Investment Fund",
                "HBL Growth Fund",
                "HBL Islamic Dedicated Equity Fund",
                "HBL Stock Fund"
            ))
            itemBinding.subRv.apply {
                adapter = faqMutualFundsSubAdapter
                layoutManager = GridLayoutManager(this.context,1)
            }

            itemBinding.subRv.isNestedScrollingEnabled = false

            itemBinding.dropdownBtn.setOnClickListener {
                if (!itemBinding.subLayout.isVisible) {
                    itemBinding.subLayout.visibility = View.VISIBLE
                    itemBinding.dropdownBtn.rotation = 0f
                }
                else {
                    itemBinding.subLayout.visibility = View.GONE
                    itemBinding.dropdownBtn.rotation = 270f
                }
            }
        }
    }
}