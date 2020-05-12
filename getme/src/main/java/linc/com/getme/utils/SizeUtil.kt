package linc.com.getme.utils

class SizeUtil {

    companion object {

        private val titles = arrayOf("B","KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        fun format(bytes: Long): String {
            var size: Double = bytes.toDouble()
            var counter = 0
            while(true) {
                if(size > 100) {
                    size /= 1024
                    counter++
                } else
                    break
            }
            return " %.2f ${titles[counter]}".format(size)
        }

        fun toKilobytes(bytes: Long) = bytes/1024
        fun toMegabytes (bytes: Long) = toKilobytes(bytes)/1024
        fun toGigabytes(bytes: Long) = toMegabytes(bytes)/1024
        fun toTerabytes(bytes: Long) = toGigabytes(bytes) /1024
        fun toPetabytes(bytes: Long) = toTerabytes(bytes) /1024
        fun toExabytes(bytes: Long) = toPetabytes(bytes) /1024
        fun toZettabytes(bytes: Long) = toExabytes(bytes) /1024
        fun toYottabytes(bytes: Long) = toZettabytes(bytes) /1024

    }

}