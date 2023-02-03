package com.hbl.amc.ui.Notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val view = binding.root
        initViews()
        return view
    }

    fun initViews() {
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        val  adapter = NotificationHeaderAdapter()
        binding.notificationRecyclerview.adapter = adapter
        adapter.updateData(addDummyData())



    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    fun addDummyData(): List<NotificationHeader>{
        var list = mutableListOf<NotificationHeader>()
        var notificationList = mutableListOf<Notification>()
        notificationList.add(Notification(1, "just now", "Fixed Income Fund", getString(R.string.dummy_text)))
        notificationList.add(Notification(2, "1 min ago", "Fixed Income Fund", getString(R.string.dummy_text)))
        notificationList.add(Notification(3, "1 hour ago", "Fixed Income Fund", getString(R.string.dummy_text)))
        list.add(NotificationHeader(1, "Today", notificationList))
        list.add(NotificationHeader(1, "2 September 2021", notificationList))
        list.add(NotificationHeader(1, "28 August 2021", notificationList))
        list.add(NotificationHeader(1, "25 August 2021", notificationList))
        return list
    }
}