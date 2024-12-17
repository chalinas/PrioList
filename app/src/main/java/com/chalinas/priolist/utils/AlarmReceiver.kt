package com.chalinas.priolist.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("TAG", "onReceive: alarm received", )
        val taskId = intent.getStringExtra("TASK_ID") ?: return
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Unknown Task"
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION") ?: ""

        AppNotificationManager.sendTaskNotification(context, taskId, taskTitle, taskDescription)
    }
}
