package thierry.friends.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import thierry.friends.R
import thierry.friends.ui.mainactivity.MainActivity

class FcmMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        Log.i("THIERRYBITAR", newToken)
        super.onNewToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("THIERRYBITAR", "From: ${remoteMessage.from}")
        Log.i("THIERRYBITAR", "Message data payload: ${remoteMessage.data}")

        if (remoteMessage.notification != null) {
            val body = remoteMessage.notification!!.body
            val title = remoteMessage.notification!!.title
            Log.i("THIERRYBITAR", "Message Notification Title: $title")
            Log.i("THIERRYBITAR", "Message Notification Body: $body")
            if (!MainActivity.isChatFragmentOpen()) {
                showNotification(title.toString(), body.toString())
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "fcmchannel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "MyFcmNotification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "This is FCM Notification"
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = androidx.core.app.NotificationCompat.Builder(this, channelId)
        builder.setAutoCancel(true).setContentText(body).setContentTitle(title)
            .setSmallIcon(R.drawable.baseline_chat_24)
        notificationManager.notify(100, builder.build())
    }

}