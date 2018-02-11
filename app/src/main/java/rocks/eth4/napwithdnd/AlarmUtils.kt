package rocks.eth4.napwithdnd

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.SystemClock
import android.widget.Toast

/**
 * Created by eth4 on 8/2/18.
 */
class AlarmUtils {

    companion object {

        fun scheduleAlarm(context: Context, durationInMinute: Int){
            val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_FIRE_ALARM
            val pendingIntent = PendingIntent.getBroadcast(context, AlarmBroadcastReceiver.REQUEST_CODE_FIRE_ALARM, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (durationInMinute)*60*1000,
                    pendingIntent
            )
            if (AlarmUtils.isAlarmScheduled(context)) {
                NotificationUtils.createUpcomingAlarmNotification(context, durationInMinute)
            }
        }

        fun isAlarmScheduled(context: Context): Boolean{
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_FIRE_ALARM
            val isScheduled = PendingIntent.getBroadcast(context, AlarmBroadcastReceiver.REQUEST_CODE_FIRE_ALARM, intent, PendingIntent.FLAG_NO_CREATE) != null
            return isScheduled
        }

        fun cancelScheduledAlarm(context: Context) {
            val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = AlarmBroadcastReceiver.ACTION_FIRE_ALARM
            val pendingIntent = PendingIntent.getBroadcast(context, AlarmBroadcastReceiver.REQUEST_CODE_FIRE_ALARM, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        fun stopAlarm(context: Context){
            cancelScheduledAlarm(context)
            NotificationUtils.cancelUpcomingAlarmNotification(context)
            Toast.makeText(context, context.getString(R.string.alarm_dismissed), Toast.LENGTH_SHORT).show()
        }
    }
}
