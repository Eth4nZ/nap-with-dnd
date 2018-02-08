package rocks.eth4.napwithdnd

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast

/**
 * Created by eth4 on 7/2/18.
 */

class AppUtils() {


    companion object {

        @RequiresApi(Build.VERSION_CODES.M)
        fun turnOnDoNotDisturb(context: Context) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mNotificationManager.isNotificationPolicyAccessGranted) {
                if (mNotificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
                    Toast.makeText(context, context.getString(R.string.do_not_disturb_enabled), Toast.LENGTH_SHORT).show();
                }
            } else {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
                Toast.makeText(context, context.getString(R.string.do_not_disturb_permission_request), Toast.LENGTH_SHORT).show();
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun turnOffDoNotDisturb(context: Context){
             val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mNotificationManager.isNotificationPolicyAccessGranted) {
                if (mNotificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALARMS) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    Toast.makeText(context, context.getString(R.string.do_not_disturb_enabled), Toast.LENGTH_SHORT).show();
                }
            } else {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
                Toast.makeText(context, context.getString(R.string.do_not_disturb_permission_request), Toast.LENGTH_SHORT).show();
            }

        }




    }
}