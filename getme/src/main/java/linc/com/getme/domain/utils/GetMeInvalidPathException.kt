package linc.com.getme.domain.utils

import java.lang.Exception

internal class GetMeInvalidPathException : Exception() {
    override val message: String?
        get() = "You try to use an invalid path or path to FILE. Be careful and try to use the correct path to DIRECTORY"
}