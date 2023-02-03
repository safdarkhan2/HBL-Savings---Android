package com.hbl.amc.ui.Profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.DocumentChecklistItemBinding
import com.hbl.amc.domain.model.Documents
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.domain.model.DocumentsTypesResult
import kotlin.properties.Delegates

class ItemProfilePendingDocumentsAdapter :
    RecyclerView.Adapter<ItemProfilePendingDocumentsAdapter.ProfilePendingDocumentsViewHolder>() {

    private var pendingDocList: List<Documents> by Delegates.observable(emptyList()) { _, _, _ ->
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
    ): ProfilePendingDocumentsViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemBinding =
            DocumentChecklistItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        val holder = ProfilePendingDocumentsViewHolder(itemBinding)


        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)

    override fun onBindViewHolder(viewHolder: ProfilePendingDocumentsViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        viewHolder.textView.text = dataSet[position]
        if (position != RecyclerView.NO_POSITION) {
            val pendingDoc = pendingDocList[position]
            viewHolder.bind(pendingDoc)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = pendingDocList.size

    fun setPendingDoc(pendingDoc: List<Documents>) {
        pendingDocList = pendingDoc
    }

    fun setDocumentTypes(updatedDocumentsList: List<DocumentsTypesResult>) {
        documentTypes = updatedDocumentsList
    }

    inner class ProfilePendingDocumentsViewHolder(private val itemBinding: DocumentChecklistItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(pendingDoc: Documents) {
            itemBinding.checkMark.visibility = GONE
            val document = documentTypes?.find { dc -> dc.id == pendingDoc.documentTypeId }
            itemBinding.tileTitle.text = document?.description
//            itemBinding.tileTitle.text = pendingDoc.documentName
            itemBinding.uploadBtn.visibility = GONE
            itemBinding.editBtn.visibility = GONE
        }
    }


}