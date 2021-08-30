package com.teamolj.teamochat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "메시지서비스"

    // Called if the FCM registration token is updated: 이미 초기생성이 된 상태일 때 작동
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    // Foreground에서만 작동
    // Background 수신 메시지 클릭 시의 처리는 LoginActivity에서 확인 (data payload 활용)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // 메시지의 notification payload 받아오기
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // 작업표시줄에 알림 띄우기 (Foreground이므로 수작업 필요)
            sendNotification(it.title, it.body)
        }
    }

    // 작업표시줄에 알림 띄우는 함수
    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, NotificationActivity::class.java)   // 알림 클릭 시 띄울 activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.tc_notification_icon)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0(API 수준 26)부터는 모든 알림을 채널에 할당해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "푸시 알림", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}