package com.hbl.amc.ui.productInformation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.R
import com.hbl.amc.databinding.ItemProfileMutualFundBinding
import com.hbl.amc.databinding.ItemVpsFundCategoriesBinding
import com.hbl.amc.domain.model.MutualFunds
import com.hbl.amc.domain.model.VPSFundCategory
import com.hbl.amc.ui.Profile.ItemProfileMutualFundsAdapter
import kotlin.properties.Delegates

class VPSFundsCategoriesAdapter (private val context: Context, private val onCategoryClick : (vpsFundCategory : VPSFundCategory) -> Unit) : RecyclerView.Adapter<VPSFundsCategoriesAdapter.VPSFundsCategoriesViewHolder>() {

    var vpsFundsCategoryList: List<VPSFundCategory> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VPSFundsCategoriesViewHolder {
        val itemBinding =
            ItemVpsFundCategoriesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        val holder = VPSFundsCategoriesViewHolder(itemBinding)

        itemBinding.root.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                val vpsFundCategory = vpsFundsCategoryList[holder.bindingAdapterPosition]
                onCategoryClick.invoke(vpsFundCategory)
            }
        }

        itemBinding.fundTv.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                val vpsFundCategory = vpsFundsCategoryList[holder.bindingAdapterPosition]
                onCategoryClick.invoke(vpsFundCategory)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: VPSFundsCategoriesViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val vpsCatgeory = vpsFundsCategoryList.get(position)
            holder.bind(vpsCatgeory, context)
        }
    }

    fun setCategoriesData(categories : List<VPSFundCategory>) {
        vpsFundsCategoryList = categories
    }

    override fun getItemCount(): Int = vpsFundsCategoryList.size

    class VPSFundsCategoriesViewHolder(private val itemBinding: ItemVpsFundCategoriesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: VPSFundCategory?, context: Context) {
            itemBinding.fundTv.text = fund?.name

            if (fund?.isSelected == true) {
                itemBinding.fundTv.setTextColor(ContextCompat.getColor(context, R.color.hbl_main_green))
            } else {
                itemBinding.fundTv.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            }

            itemBinding.fundTv.isChecked = fund?.isSelected!!
//            var desc = fund?.description
//            itemBinding.expandTextView.text = "$desc \n"
        }
    }
}