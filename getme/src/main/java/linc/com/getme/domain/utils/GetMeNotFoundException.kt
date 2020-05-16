package linc.com.getme.domain.utils

import java.lang.Exception

class GetMeNotFoundException : Exception() {
    override val message: String?
        get() = "You try to use GetMe when it was CLOSED or some view or callback REFER to GetMe. Check if you called show() method or remove views and callbacks in constructor that can refer to GetMe"
}