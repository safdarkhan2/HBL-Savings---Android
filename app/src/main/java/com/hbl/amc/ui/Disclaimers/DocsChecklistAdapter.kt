package com.hbl.amc.ui.Disclaimers

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.DocumentChecklistItemBinding
import com.hbl.amc.databinding.ItemFundBinding
import com.hbl.amc.domain.model.DocumentChecklistData
import com.hbl.amc.domain.model.DocumentChecklistResult
import com.hbl.amc.domain.model.Fund
import com.hbl.amc.ui.productInformation.FundsAdapter
import kotlin.properties.Delegates

class DocsChecklistAdapter(private val onUploadOrEditClick: (doc : DocumentChecklistData) -> Unit) : RecyclerView.Adapter<DocsChecklistAdapter.DocChecklistViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var docCheckList: List<DocumentChecklistData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocChecklistViewHolder {
        val itemBinding =
            DocumentChecklistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = DocChecklistViewHolder(itemBinding = itemBinding)

        itemBinding.uploadBtn.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onUploadOrEditClick.invoke(docCheckList[holder.bindingAdapterPosition])
            }
        }

        itemBinding.editBtn.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onUploadOrEditClick.invoke(docCheckList[holder.bindingAdapterPosition])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: DocChecklistViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val document = docCheckList[position]
            holder.bind(document)
        }
    }

    override fun getItemCount() = docCheckList.size

    fun setDocumentsChecklist(updatedDocChecklist : List<DocumentChecklistData>) {
        docCheckList = updatedDocChecklist
    }

    class DocChecklistViewHolder(private val itemBinding : DocumentChecklistItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(docItem : DocumentChecklistData) {
            itemBinding.tileTitle.text = docItem.description

            if (docItem.isUploaded) {
                itemBinding.checkMark.visibility = VISIBLE
                itemBinding.uploadBtn.visibility = GONE
            } else {
                itemBinding.checkMark.visibility = INVISIBLE
                itemBinding.uploadBtn.visibility = VISIBLE
            }

            if (docItem.isUploaded && docItem.isEditable) {
                itemBinding.editBtn.visibility = VISIBLE
            } else {
                itemBinding.editBtn.visibility = GONE
            }

            /*if (docItem.isRequired && !docItem.isUploaded) {
                itemBinding.errorTv.visibility = VISIBLE
            } else {
                itemBinding.errorTv.visibility = GONE
            }*/
        }
    }
}