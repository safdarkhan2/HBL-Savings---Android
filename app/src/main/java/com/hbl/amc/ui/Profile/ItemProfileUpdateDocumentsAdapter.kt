package com.hbl.amc.ui.Profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.DocumentChecklistItemBinding
import com.hbl.amc.domain.model.Documents
import com.hbl.amc.domain.model.DocumentsTypesResult
import kotlin.properties.Delegates

class ItemProfileUpdateDocumentsAdapter() :
    RecyclerView.Adapter<ItemProfileUpdateDocumentsAdapter.ProfileUpdateDocumentsViewHolder>() {

    private var updateDocList: List<Documents> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var documentTypes : List<DocumentsTypesResult>? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ProfileUpdateDocumentsViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemBinding =
            DocumentChecklistItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        val holder = ProfileUpdateDocumentsViewHolder(itemBinding)


        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)

    override fun onBindViewHolder(viewHolder: ProfileUpdateDocumentsViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        viewHolder.textView.text = dataSet[position]
        if (position != RecyclerView.NO_POSITION) {
            val updateDoc = updateDocList[position]
            viewHolder.bind(updateDoc)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = updateDocList.size

    fun setUpdateDoc(updateDoc: List<Documents>) {
        updateDocList = updateDoc
    }

    fun setDocumentTypes(updatedDocumentsList: List<DocumentsTypesResult>) {
        documentTypes = updatedDocumentsList
    }

    inner class ProfileUpdateDocumentsViewHolder(private val itemBinding: DocumentChecklistItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(updateDoc: Documents) {
            itemBinding.checkMark.visibility = View.GONE
            val document = documentTypes?.find { dc -> dc.id == updateDoc.documentTypeId }
            itemBinding.tileTitle.text = document?.description
//            itemBinding.tileTitle.text = updateDoc.documentName
            itemBinding.uploadBtn.visibility = View.GONE
            itemBinding.editBtn.visibility = View.GONE
        }
    }


}