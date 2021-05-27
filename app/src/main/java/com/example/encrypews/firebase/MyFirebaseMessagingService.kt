package com.example.encrypews.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.encryption.EncryptionDecryption
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService:FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data["sentText"]?.let { Log.d("Msg", it) }
        val sentText = remoteMessage.data["sentText"]
        remoteMessage.notification?.let {
            if (sentText != null) {
                showNotification(it,sentText)
            }
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("New token",token)
        sendRegistration(token)
    }

    private fun sendRegistration(token:String){
        val userId = MyFireBaseAuth.getUserId()
        val ref = Firebase.database.reference.child(com.example.encrypews.Utils.Constants.USERS)
        if(userId != ""){
            ref.child(userId).child("deviceToken").setValue(token)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(notification:RemoteMessage.Notification,string: String){
        val notificationManager : NotificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,"Notification",
            NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.description ="app encyrpews"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor  = getColor(R.color.blue)

            val longArray = LongArray(4)
            longArray[0] =0
            longArray[1]=1000
            longArray[2]=500
            longArray[3]=1000
            notificationChannel.vibrationPattern = longArray

            notificationManager.createNotificationChannel(notificationChannel)

        }
        val notificationBuilder = NotificationCompat.Builder(this,Constants.NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(notification.title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(EncryptionDecryption().decrypt(string))
            .setContentInfo("Info")

        notificationManager.notify(0,notificationBuilder.build())
    }
}