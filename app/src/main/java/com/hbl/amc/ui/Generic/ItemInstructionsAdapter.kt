package com.hbl.amc.ui.Generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemInstructionsBinding
import com.hbl.amc.domain.model.RedemptionInstructions
import kotlin.properties.Delegates

class ItemInstructionsAdapter() :
    RecyclerView.Adapter<ItemInstructionsAdapter.InstructionsViewHolder>() {

    private var instructionsList: List<RedemptionInstructions> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): InstructionsViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemBinding =
            ItemInstructionsBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        val holder = InstructionsViewHolder(itemBinding)


        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)

    override fun onBindViewHolder(viewHolder: InstructionsViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        viewHolder.textView.text = dataSet[position]
        if (position != RecyclerView.NO_POSITION) {
            val instruction = instructionsList[position]
            viewHolder.bind(instruction)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = instructionsList.size

    fun setInstructions(instructions: List<RedemptionInstructions>) {
        instructionsList = instructions
    }

    class InstructionsViewHolder(private val itemBinding: ItemInstructionsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(redemptionInstructions: RedemptionInstructions) {
            itemBinding.instructionTitle.text = redemptionInstructions.title
            var desc = redemptionInstructions.description
            itemBinding.expandTextView.text = desc + " \n"
        }
    }


}