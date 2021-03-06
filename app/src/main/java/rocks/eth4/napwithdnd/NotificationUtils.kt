package rocks.eth4.napwithdnd

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by eth4 on 8/2/18.
 */
class NotificationUtils {


    companion object {
        fun createUpcomingAlarmNotification(context: Context, durationInMinute: Int) {
            val channelId =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getUpcomingAlarmNotificationChannelId(context)
                    } else {
                        // If earlier version channel ID is not used
                        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                        ""
                    }
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_alarm_notification)
                    .setContentTitle(context.getString(R.string.upcoming_alarm))
                    .setContentText(getFiringTimeString(durationInMinute))
                    .setSound(null)
                    .setVibrate(null)

            val openActivityIntent = Intent(context, MainActivity::class.java)
            openActivityIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val openActivityPendingIntent = PendingIntent.getActivity(context, 0, openActivityIntent, 0)
            notificationBuilder.setContentIntent(openActivityPendingIntent)

            val cancelAlarmIntent = Intent(context, AlarmBroadcastReceiver::class.java)
            cancelAlarmIntent.action = AlarmBroadcastReceiver.ACTION_DISMISS_ALARM
            val cancelAlarmPendingIntent = PendingIntent.getBroadcast(context, AlarmBroadcastReceiver.REQUEST_CODE_DISMISS_ALARM, cancelAlarmIntent, 0)
            notificationBuilder.addAction(R.drawable.ic_alarm_notification, context.getString(R.string.dismiss_alarm), cancelAlarmPendingIntent)

            val mNotifyManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotifyManager.notify(UPCOMING_ALARM_NOTIFICATION_ID, notificationBuilder.build());
        }

        fun cancelUpcomingAlarmNotification(context: Context){
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(UPCOMING_ALARM_NOTIFICATION_ID)
        }

        fun getFiringAlarmNotification(context: Context): Notification {
            val channelId =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getFiringAlarmNotificationChannelId(context)
                    } else {
                        // If earlier version channel ID is not used
                        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                        ""
                    }
            val notificationBuilder = NotificationCompat.Builder(context, channelId)

            notificationBuilder.setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_alarm_notification)
                    .setContentTitle(context.getString(R.string.alarm_service_noti_content_title))
                    .setContentText(context.getString(R.string.alarm_service_noti_content_text))
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .priority = NotificationCompat.PRIORITY_HIGH

            val stopSelfIntent = Intent(context, AlarmService::class.java)
            stopSelfIntent.action = ACTION_STOP_SERVICE
            val stopSelfPendingIntent = PendingIntent.getService(context, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            notificationBuilder.addAction(R.drawable.ic_alarm_notification, context.getString(R.string.dismiss_alarm), stopSelfPendingIntent)

            val openActivityIntent = Intent(context, FiringAlarmActivity::class.java)
            openActivityIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val openActivityPendingIntent = PendingIntent.getActivity(context, 0, openActivityIntent, 0)
            notificationBuilder.setContentIntent(openActivityPendingIntent)

            return notificationBuilder.build()
        }



        @TargetApi(Build.VERSION_CODES.O)
        private fun getUpcomingAlarmNotificationChannelId(context: Context): String {
            val channelId = UPCOMING_ALARM_CHANNEL_ID
            val channelName = UPCOMING_ALARM_CHANNEL_NAME
            val channel = NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            return channelId
        }


        @TargetApi(Build.VERSION_CODES.O)
        private fun getFiringAlarmNotificationChannelId(context: Context): String {
            val channelId = FIRING_ALARM_CHANNEL_ID
            val channelName = FIRING_ALARM_CHANNEL_NAME
            val channel = NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.lightColor = Color.GREEN
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            return channelId
        }

        fun getFiringTimeString(durationInMinute: Int): String {
            val firingTimestamp = Calendar.getInstance().timeInMillis + durationInMinute*60*1000
            return convertTimestampToTimeString(firingTimestamp)
        }

        fun convertTimestampToTimeString(timestamp: Long): String {
            return SimpleDateFormat("EEE HH:mm", Locale.getDefault()).format(timestamp)
        }
    }
}
