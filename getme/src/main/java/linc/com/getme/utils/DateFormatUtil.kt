package linc.com.getme.utils

import java.text.SimpleDateFormat
import java.util.*

internal class DateFormatUtil {

    companion object {
        private val fullFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        fun formatFromLong(millis: Long): String =
            "${fullFormat.format(millis)} at ${timeFormat.format(millis)}"
    }

}