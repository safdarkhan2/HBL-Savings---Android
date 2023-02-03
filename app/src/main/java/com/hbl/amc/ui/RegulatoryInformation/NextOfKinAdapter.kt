package com.hbl.amc.ui.RegulatoryInformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.NomineeItemBinding
import com.hbl.amc.domain.model.NextOfKin
import kotlin.properties.Delegates

class NextOfKinAdapter (private val onActionPerform: (nextOfKin : NextOfKin, type : Int) -> Unit) : RecyclerView.Adapter<NextOfKinAdapter.NextOfKinViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var nomineeList: List<NextOfKin> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextOfKinViewHolder {
        val itemBinding =
            NomineeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = NextOfKinViewHolder(itemBinding)

        itemBinding.delete.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                val nextOfKin = nomineeList[holder.bindingAdapterPosition]
                onActionPerform.invoke(nextOfKin, 2)
            }
        }

        itemBinding.edit.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                val nextOfKin = nomineeList[holder.bindingAdapterPosition]
                onActionPerform.invoke(nextOfKin, 1)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: NextOfKinViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val nominee = nomineeList[position]
            holder.bind(nominee)
        }
    }

    override fun getItemCount() = nomineeList.size

    fun setNominees(latestNominees: List<NextOfKin>) {
        nomineeList = latestNominees
    }

    class NextOfKinViewHolder (private val itemBinding: NomineeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(nextOfKin: NextOfKin) {
            itemBinding.text1.text = nextOfKin.name
        }
    }
}