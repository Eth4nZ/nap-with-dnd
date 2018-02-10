package rocks.eth4.napwithdnd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * Created by eth4 on 10/2/18.
 */
class UpcomingNotificationActionBroadcastReceiver : BroadcastReceiver() {

    companion object {
        val TAG = UpcomingNotificationActionBroadcastReceiver::class.java.simpleName!!
        const val ACTION_DISMISS_ALARM = "rocks.eth4.napwithdnd.action.dismiss.alarm"
    }



    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null){
            Log.e(TAG, "upcoming notification action broadcast received, however context is null")
            return
        }
        else
            Log.d(TAG, "upcoming notification action broadcast received")

        if (intent?.action == ACTION_DISMISS_ALARM) {
            AlarmUtils.stopAlarm(context)
        }

    }


}