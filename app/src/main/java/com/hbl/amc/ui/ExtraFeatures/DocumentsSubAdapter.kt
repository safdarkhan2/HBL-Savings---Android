package com.hbl.amc.ui.ExtraFeatures

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.ItemDocumentsSubBinding
import kotlin.properties.Delegates

class DocumentsSubAdapter :
    RecyclerView.Adapter<DocumentsSubAdapter.ItemViewHolder>() {

    private var itemList: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentsSubAdapter.ItemViewHolder {
        val itemBinding =
            ItemDocumentsSubBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = DocumentsSubAdapter.ItemViewHolder(itemBinding)

        return holder
    }

    override fun onBindViewHolder(
        holder: DocumentsSubAdapter.ItemViewHolder,
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

    class ItemViewHolder(private val itemBinding: ItemDocumentsSubBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
//            itemBinding.fundNameTv.text = "Fixed Income Funds (20%)"
//            Log.d("item_document","test" )
        }
    }
}