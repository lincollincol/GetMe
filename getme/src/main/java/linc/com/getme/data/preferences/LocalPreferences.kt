package linc.com.getme.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import linc.com.getme.domain.data.LocalFastStorage
import linc.com.getme.utils.Constants.Companion.KEY_STACK_STATE
import org.json.JSONArray
import java.util.*


internal class LocalPreferences(private val context: Context) :
    LocalFastStorage {

    override fun clearLocalStorage() {
        getEditor().remove(KEY_STACK_STATE)
            .commit()
    }

    override fun saveStack(stack: Stack<String>) {
        getEditor().putString(KEY_STACK_STATE, JSONArray(stack.toArray()).toString())
            .commit()
    }

    override fun getStack(): Stack<String> {
        return Stack<String>().apply {
            val jsonStates = getPreferences().getString(KEY_STACK_STATE, null)

            if(jsonStates.isNullOrEmpty()) {
                return@apply
            }

            val arr = JSONArray(jsonStates)

            for(i in 0 until arr.length()) {
                push(arr[i] as String)
            }
        }

    }

    private fun getEditor(): SharedPreferences.Editor {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
    }


}