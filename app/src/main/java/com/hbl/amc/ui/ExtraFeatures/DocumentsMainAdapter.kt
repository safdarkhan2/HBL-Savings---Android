package com.hbl.amc.ui.ExtraFeatures

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemDocumentMainBinding
import kotlin.properties.Delegates

class DocumentsMainAdapter :
    RecyclerView.Adapter<DocumentsMainAdapter.ItemViewHolder>() {

    private var itemList: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentsMainAdapter.ItemViewHolder {
        val itemBinding =
            ItemDocumentMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = DocumentsMainAdapter.ItemViewHolder(itemBinding)

        return holder
    }

    override fun onBindViewHolder(
        holder: DocumentsMainAdapter.ItemViewHolder,
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

    class ItemViewHolder(private val itemBinding: ItemDocumentMainBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var documentSubAdapter : DocumentsSubAdapter

        fun bind() {
            itemBinding.fundNameTv.text = "Fixed Income Funds (20%)"
            itemBinding.documentDropdownBtn.setOnClickListener {
                if (!itemBinding.documentSubLayout.isVisible) {
                    itemBinding.documentSubLayout.visibility = View.VISIBLE
                    itemBinding.documentDropdownBtn.rotation = 0f
                }
                else {
                    itemBinding.documentSubLayout.visibility = View.GONE
                    itemBinding.documentDropdownBtn.rotation = 270f
                }
            }

            documentSubAdapter = DocumentsSubAdapter()
            itemBinding.documentSubRv.apply {
                adapter = documentSubAdapter
                layoutManager = GridLayoutManager(this.context,1)
            }
            documentSubAdapter.updateData(listOf(
                "HBL Investment Fund",
                "HBL Growth Fund",
                "HBL Islamic Dedicated Equity Fund",
                "HBL Stock Fund"
            ))
        }
    }

}
