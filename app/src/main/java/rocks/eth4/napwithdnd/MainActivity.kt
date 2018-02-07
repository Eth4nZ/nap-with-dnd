package rocks.eth4.napwithdnd

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName!!
        var notificationId = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val btnOneMin: Button by lazy { findViewById<Button>(R.id.set_alarm_1_min) }

        btnOneMin.clicks()
                .doOnNext {
                    val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                            0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

                    alarmManager.setExact(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + 5*1000,
                            pendingIntent
                    )
                    Toast.makeText(applicationContext, getString(R.string.alarm_set), Toast.LENGTH_SHORT).show()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ ->
                            Log.d(TAG, "clicked")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                turnOnDoNotDisturb()
                            }
                        },
                        { error -> Log.e(TAG, error.message) }
                )



    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun turnOnDoNotDisturb(){
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (mNotificationManager.isNotificationPolicyAccessGranted) {
            if(mNotificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
                Toast.makeText(applicationContext, getString(R.string.do_not_disturb_enabled), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
            Toast.makeText(applicationContext, getString(R.string.do_not_disturb_permission_request), Toast.LENGTH_SHORT).show();
        }
    }
}
