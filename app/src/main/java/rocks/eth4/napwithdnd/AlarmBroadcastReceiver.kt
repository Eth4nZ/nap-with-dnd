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




/**
 * Created by eth4 on 7/2/18.
 */
class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        val TAG = AlarmBroadcastReceiver::class.java.simpleName!!
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "received")
        val mIntent = Intent(context, AlarmService::class.java)
        context?.stopService(mIntent)
        context?.startService(mIntent)
    }


}