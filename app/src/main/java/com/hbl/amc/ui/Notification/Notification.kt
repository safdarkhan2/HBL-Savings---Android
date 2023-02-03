package com.hbl.amc.ui.Notification

import java.util.*

data class Notification(
    val id : Int,
    val time : String,
    val title: String,
    val description : String
)

data class NotificationHeader(
    val id : Int,
    val date : String,
    val notificationList : List<Notification>
)