package linc.com.getme.domain

import java.util.*

interface LocalFastStorage {

    fun saveStack(stack: Stack<String>)
    fun getStack(): Stack<String>
    fun clearLocalStorage()

}