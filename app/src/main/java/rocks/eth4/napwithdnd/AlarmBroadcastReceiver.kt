package rocks.eth4.napwithdnd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import java.io.IOException
import android.media.RingtoneManager
import android.os.Build


/**
 * Created by eth4 on 7/2/18.
 */
class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        val TAG = AlarmBroadcastReceiver::class.java.simpleName!!
        const val ACTION_FIRE_ALARM = "rocks.eth4.napwithdnd.action.fire.alarm"
        const val ACTION_DISMISS_ALARM = "rocks.eth4.napwithdnd.action.dismiss.alarm"
        const val REQUEST_CODE_FIRE_ALARM = 940101
        const val REQUEST_CODE_DISMISS_ALARM = 940201
    }



    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null){
            Log.e(TAG, "alarm broadcast received, however context is null")
            return
        }
        else
            Log.v(TAG, "alarm broadcast received")

        if (intent?.action == ACTION_FIRE_ALARM) {
            val mIntent = Intent(context, AlarmService::class.java)
            context.stopService(mIntent)
            NotificationUtils.cancelUpcomingAlarmNotification(context)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                context.startForegroundService(mIntent)
            } else {
                context.startService(mIntent)
            }
        }
        else if (intent?.action == ACTION_DISMISS_ALARM) {
            AlarmUtils.stopAlarm(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DndUtils.turnOffDoNotDisturb(context)
            }
        }
    }


}