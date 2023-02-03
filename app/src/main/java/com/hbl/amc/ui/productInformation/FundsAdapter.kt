package com.hbl.amc.ui.productInformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemFundBinding
import com.hbl.amc.domain.model.Fund
import kotlin.properties.Delegates

class FundsAdapter(private val onSelectOrUnSelectFund: (position: Int, isSelected: Boolean) -> Unit)
    : RecyclerView.Adapter<FundsAdapter.FundsViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var fundsList: List<Fund> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundsViewHolder {
        val itemBinding =
            ItemFundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = FundsViewHolder(itemBinding)

        itemBinding.radio.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onSelectOrUnSelectFund.invoke(holder.adapterPosition, !fundsList[holder.adapterPosition].isSelected)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: FundsViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val fund = fundsList[position]
            holder.bind(fund)
        }
    }

    override fun getItemCount() = fundsList.size

    fun updateData(funds: List<Fund>) {
        fundsList = funds
        notifyDataSetChanged()
    }

    class FundsViewHolder (private val itemBinding: ItemFundBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: Fund) {
            itemBinding.radio.text = fund.name
            itemBinding.expandTextView.text = fund.description
            itemBinding.radio.isChecked = fund.isSelected
        }
    }
}