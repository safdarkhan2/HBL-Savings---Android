package com.hbl.amc.ui.Notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbl.amc.databinding.NotificationHeaderBinding
import com.hbl.amc.databinding.NotificationRecyclerItemBinding
import kotlin.properties.Delegates

class NotificationAdapter() :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // Our data list is going to be notified when we assign a new list of data to it
    private var notificationList: List<Notification> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val itemBinding =
            NotificationRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = NotificationViewHolder(itemBinding)
//
//        itemBinding.radio.setOnClickListener {
//            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
//                onSelectOrUnSelectFund.invoke(holder.adapterPosition, !fundsList[holder.adapterPosition].isSelected)
//            }
//        }

        return holder
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val notification = notificationList[position]
            holder.bind(notification)
        }
    }

    override fun getItemCount() = notificationList.size

    fun updateData(notificationHeader: List<Notification>) {
        notificationList = notificationHeader
        notifyDataSetChanged()
    }

    class NotificationViewHolder(private val itemBinding: NotificationRecyclerItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(notification: Notification) {

            itemBinding.notificationDetailTv.text = notification.description
            itemBinding.notificationTimeTv.text = notification.time
            itemBinding.notificationTitleTv.text = notification.title
        }
    }
}