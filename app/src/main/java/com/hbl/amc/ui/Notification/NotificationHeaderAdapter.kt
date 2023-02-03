package com.hbl.amc.ui.Notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.NotificationHeaderBinding
import kotlin.properties.Delegates

class NotificationHeaderAdapter() :
    RecyclerView.Adapter<NotificationHeaderAdapter.NotificationHeaderViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var notificationHeaderList: List<NotificationHeader> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationHeaderViewHolder {
        val itemBinding =
            NotificationHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = NotificationHeaderViewHolder(itemBinding, parent.context)
//
//        itemBinding.radio.setOnClickListener {
//            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
//                onSelectOrUnSelectFund.invoke(holder.adapterPosition, !fundsList[holder.adapterPosition].isSelected)
//            }
//        }

        return holder
    }

    override fun onBindViewHolder(holder: NotificationHeaderViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val notificationHeader = notificationHeaderList[position]
            holder.bind(notificationHeader)
        }
    }

    override fun getItemCount() = notificationHeaderList.size

    fun updateData(notificationHeader: List<NotificationHeader>) {
        notificationHeaderList = notificationHeader
        notifyDataSetChanged()
    }

    class NotificationHeaderViewHolder(
        private val itemBinding: NotificationHeaderBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(notificationHeader: NotificationHeader) {

            itemBinding.titleTv.text = notificationHeader.date
            itemBinding.innerRecyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = NotificationAdapter()
            itemBinding.innerRecyclerView.adapter = adapter
            adapter.updateData(notificationHeader.notificationList)

        }
    }
}