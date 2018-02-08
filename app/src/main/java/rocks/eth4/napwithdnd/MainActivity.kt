package rocks.eth4.napwithdnd

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import android.support.v4.app.NotificationCompat



class MainActivity : AppCompatActivity() {

    val PREF_KEY_ONES_PLACE = "pref_ones_place"
    val PREF_KEY_TENS_PLACE = "pref_tens_place"
    var value_ones_place = 0
    var value_tens_place = 0

    val btnOneMin: Button by lazy { findViewById<Button>(R.id.set_alarm_1_min) }
    val btnTest: Button by lazy { findViewById<Button>(R.id.test_noti) }
    val npTensPlace: NumberPicker by lazy { findViewById<NumberPicker>(R.id.np_tens_place)}
    val npOnesPlace: NumberPicker by lazy { findViewById<NumberPicker>(R.id.np_ones_place)}

    companion object {
        val TAG = MainActivity::class.java.simpleName!!
        var notificationId = 94020
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


        btnOneMin.clicks()
                .doOnNext {
                    val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                            0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

                    alarmManager.setExact(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (value_tens_place*10+value_ones_place)*60*1000,
                            pendingIntent
                    )

                    Toast.makeText(applicationContext, getString(R.string.alarm_set), Toast.LENGTH_SHORT).show()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ ->
                            Log.d(TAG, "clicked")
                            NotificationUtils.createUpcomingAlarmNotification(applicationContext, (value_tens_place*10+value_ones_place))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                AppUtils.turnOnDoNotDisturb(applicationContext)
                            }
                        },
                        { error -> Log.e(TAG, error.message) }
                )

        btnTest.clicks()
                .doOnNext {
                    NotificationUtils.createUpcomingAlarmNotification(applicationContext, (value_tens_place*10+value_ones_place))
                    Log.d(TAG, "test button clicked ")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(){}

        setupComponents()


    }



    private fun setupComponents() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        value_tens_place = sharedPref.getInt(PREF_KEY_TENS_PLACE, 0)
        value_ones_place = sharedPref.getInt(PREF_KEY_ONES_PLACE, 0)

        updateNumberPickerDivider(applicationContext, npTensPlace)
        npTensPlace.minValue = 0
        npTensPlace.maxValue = 9
        npTensPlace.value = value_tens_place
        npTensPlace.setOnValueChangedListener { picker, oldVal, newVal -> updateTimerValue() }

        updateNumberPickerDivider(applicationContext, npOnesPlace)
        npOnesPlace.minValue = 0
        npOnesPlace.maxValue = 9
        npOnesPlace.value = value_ones_place
        npOnesPlace.setOnValueChangedListener { picker, oldVal, newVal -> updateTimerValue() }

    }

    fun updateNumberPickerDivider(c: Context, picker: NumberPicker) {

        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    pf.set(picker, ContextCompat.getDrawable(c, R.drawable.number_picker_divider))
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }


    private fun updateTimerValue() {
        value_tens_place = npTensPlace.value
        value_ones_place = npOnesPlace.value

        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putInt(PREF_KEY_TENS_PLACE, value_tens_place)
        editor.putInt(PREF_KEY_ONES_PLACE, value_ones_place)
        editor.apply()
    }



}
