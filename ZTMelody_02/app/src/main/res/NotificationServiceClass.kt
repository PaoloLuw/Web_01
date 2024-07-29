//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.media.session.MediaButtonReceiver
//import com.luwliette.ztmelody_02.R
//
//class NotificationServiceClass(private val context: Context) {
//
//    companion object {
//        private const val NOTIFICATION_ID = 1
//        private const val CHANNEL_ID = "music_channel"
//        private const val CHANNEL_NAME = "Music Playback"
//    }
//
//    private var notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
//
//    init {
//        createNotificationChannel()
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    fun showNotification(songTitle: String, isPlaying: Boolean) {
//        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
//
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_play)
//            .setContentTitle("MyMelody")
//            .setContentText(songTitle)
//            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_play))
//            .addAction(
//                NotificationCompat.Action(
//                    R.drawable.ic_play,
//                    "Previous",
//                    null
//                )
//            )
//            .addAction(
//                NotificationCompat.Action(
//                    playPauseIcon,
//                    "Play/Pause",
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        context,
//                        androidx.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
//                    )
//                )
//            )
//            .addAction(
//                NotificationCompat.Action(
//                    R.drawable.ic_forward,
//                    "Next",
//                    null
//                )
//            )
//            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    fun cancelNotification() {
//        notificationManager.cancel(NOTIFICATION_ID)
//    }
//}
