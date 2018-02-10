package rocks.eth4.napwithdnd

import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by eth4 on 10/2/18.
 */

class TimeUnitTest {

    @Test
    fun firing_string_isCorrect() {
        val currentTimestamp: Long = 1518256143
        val durationInMinute = 70
        val firingTimestamp = currentTimestamp+durationInMinute*60*1000
        val result = NotificationUtils.convertTimestampToTimeString(firingTimestamp)
        assertEquals("Sat 21:59", result)
    }

    @Test
    fun current_time_string_isCorrect() {
        val currentTimestamp: Long = 1518256143
        val result = NotificationUtils.convertTimestampToTimeString(currentTimestamp)
        assertEquals("Sat 20:49", result)
    }

}