package linc.com.getme.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import linc.com.getme.domain.LocalFastStorage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class LocalPreferences(private val context: Context) : LocalFastStorage {

    override fun clearLocalStorage() {
        getEditor().remove("SSS")
            .commit()
    }


    override fun saveStack(stack: Stack<String>) {
        getEditor().putString("SSS", JSONArray(stack.toArray()).toString())
            .commit()
    }

    override fun getStack(): Stack<String> {
        return Stack<String>().apply {
            val jstate = getPreferences().getString("SSS", null)

            if(jstate.isNullOrEmpty()) {
                return@apply
            }

            val arr = JSONArray(jstate)

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