package com.chalinas.priolist.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chalinas.priolist.MainActivity
import com.chalinas.priolist.R

object AppNotificationManager {
    private const val CHANNEL_ID = "todo_notifications"
    private const val CHANNEL_NAME = "Task Notifications"
    private const val CHANNEL_DESC = "Notifications for tasks"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTaskNotification(context: Context, taskId: String, title: String, description: String) {
        Log.e("TAG", "sendTaskNotification: called")
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, taskId.hashCode(),
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        sendNotification(context, taskId.hashCode(), notification)
//        NotificationManagerCompat.from(context).notify(taskId.hashCode(), notification)
    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        notification: Notification
    ) {
        Log.e("TAG", "sendNotification: now sending")
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("AppAlarmManager", "sendNotification: no permission granted")
                return
            }
            notify(notificationId, notification)
        }
    }
}
