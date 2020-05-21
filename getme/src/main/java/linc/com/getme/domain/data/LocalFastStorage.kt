package linc.com.getme.domain.data

import java.util.*

internal interface LocalFastStorage {

    fun saveStack(stack: Stack<String>)
    fun getStack(): Stack<String>
    fun clearLocalStorage()

}