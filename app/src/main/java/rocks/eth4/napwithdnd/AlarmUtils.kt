package rocks.eth4.napwithdnd

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.SystemClock

/**
 * Created by eth4 on 8/2/18.
 */
class AlarmUtils() {

    companion object {

        fun scheduleAlarm(context: Context, durationInMin: Int){
            val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_ALARM_BROADCAST_RECEIVER
            val pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (durationInMin)*60*1000,
                    pendingIntent
            )
        }

        fun isAlarmScheduled(context: Context): Boolean{
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_ALARM_BROADCAST_RECEIVER
            val isScheduled = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null
            return isScheduled
        }

        fun stopAlarm(context: Context) {
            val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_ALARM_BROADCAST_RECEIVER
            val pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
