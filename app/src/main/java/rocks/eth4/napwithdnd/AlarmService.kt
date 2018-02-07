package rocks.eth4.napwithdnd

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.io.IOException

/**
 * Created by eth4 on 7/2/18.
 */

class AlarmService: Service() {
    companion object {
        private val TAG = AlarmService::class.java.simpleName
        private val ACTION_STOP_SERVICE = "rocks.eth4.napwithdnd.ACTION_STOP_SERVICE"
        private val NOTIFICATION_ID = 8964000
    }

    private val mMediaPlayer = MediaPlayer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (ACTION_STOP_SERVICE == intent.action) {
            Log.d(TAG, "called to cancel service")
            mMediaPlayer.stop()
            stopSelf()
            return Service.START_REDELIVER_INTENT
        }


        createForegroundNotification()
        playSound(context = applicationContext, alert = getAlarmUri())

        return Service.START_REDELIVER_INTENT
    }

    private fun playSound(context: Context, alert: Uri) {
        try {
            mMediaPlayer.setDataSource(context, alert)
            val audioManager = context
                    .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
                mMediaPlayer.prepare()
                mMediaPlayer.start()
            }
        } catch (e: IOException) {
            println("OOPS")
        }

    }

    private fun getAlarmUri(): Uri {
        var alert: Uri? = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        return alert!!
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createForegroundNotification() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        notificationBuilder.setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_dnd_nap_silhouette)
                .setContentTitle(getString(R.string.alarm_service_noti_content_title))
                .setContentText(getString(R.string.alarm_service_noti_content_text))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .priority = NotificationCompat.PRIORITY_HIGH

        val stopSelf = Intent(this, AlarmService::class.java)
        stopSelf.action = ACTION_STOP_SERVICE
        val pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT)
        notificationBuilder.addAction(R.drawable.ic_access_alarm_black_24dp, "Stop Timer", pStopSelf)
        //        manager.notify(NOTIFICATION_ID, builder.build());
        val notification = notificationBuilder.build()

        startForeground(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "nap_with_dnd_service"
        val channelName = "Nap with DND service"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DndUtil.turnOffDoNotDisturb(applicationContext)
        }
    }
}