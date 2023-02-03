package com.hbl.amc.ui.Profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemProfileMutualFundBinding
import com.hbl.amc.domain.model.MutualFunds
import kotlin.properties.Delegates

class ItemProfileMutualFundsAdapter :
    RecyclerView.Adapter<ItemProfileMutualFundsAdapter.ProfileMutualFundsViewHolder>() {

    private var mutualFundsList: List<MutualFunds> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ProfileMutualFundsViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemBinding =
            ItemProfileMutualFundBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )


        return ProfileMutualFundsViewHolder(itemBinding)
    }

    // Replace the contents of a view (invoked by the layout manager)

    override fun onBindViewHolder(viewHolder: ProfileMutualFundsViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        viewHolder.textView.text = dataSet[position]
        if (position != RecyclerView.NO_POSITION) {
            val mutualFund = mutualFundsList.get(position)
            viewHolder.bind(mutualFund)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mutualFundsList.size

    fun setMutualFunds(fund: List<MutualFunds>) {

        mutualFundsList = fund

    }

    class ProfileMutualFundsViewHolder(private val itemBinding: ItemProfileMutualFundBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: MutualFunds?) {
            itemBinding.fundTv.text = fund?.name
            var desc = fund?.description
            itemBinding.expandTextView.text = "$desc \n"
        }
    }


}