package com.teamolj.teamochat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "메시지서비스"

    // Called if the FCM registration token is updated: 이미 초기생성이 된 상태일 때 작동
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
}