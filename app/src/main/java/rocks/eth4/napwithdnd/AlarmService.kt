package rocks.eth4.napwithdnd

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.io.IOException

/**
 * Created by eth4 on 7/2/18.
 */

class AlarmService: Service() {
    companion object {
        private val TAG = AlarmService::class.java.simpleName
    }

    private val mMediaPlayer = MediaPlayer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (ACTION_STOP_SERVICE == intent.action) {
            Log.v(TAG, "called to cancel alarm service")
            mMediaPlayer.stop()
            stopSelf()
            return Service.START_REDELIVER_INTENT
        }


        val notification = NotificationUtils.getFiringAlarmNotification(applicationContext)
        startForeground(FIRING_ALARM_NOTIFICATION_ID, notification)
        val alarmUri = getAlarmUri()
        if (alarmUri != null)
            playSound(context = applicationContext, alert = alarmUri)
        else
            Log.e(TAG, "Failed to load alarm URI")

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
            Log.e(TAG, e.toString())
        }

    }

    private fun getAlarmUri(): Uri? {
        var alert: Uri? = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alert == null) {
            Log.e(TAG, "Failed to load alarm uri")
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        return alert
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }




   override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DndUtils.turnOffDoNotDisturb(applicationContext)
        }
    }
}