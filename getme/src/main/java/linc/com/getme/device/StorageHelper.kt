package linc.com.getme.device


import android.content.Context
import android.os.storage.StorageManager
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

internal class StorageHelper(
    private val context: Context
) {

    fun getExternalStoragePath(isRemovableStorage: Boolean): String? {
        val mStorageManager =
            context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumeClazz: Class<*>
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList: Method = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath: Method = storageVolumeClazz.getMethod("getPath")
            val isRemovable: Method = storageVolumeClazz.getMethod("isRemovable")
            val result: Any = getVolumeList.invoke(mStorageManager)
            val length: Int = Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement: Any = Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable =
                    isRemovable.invoke(storageVolumeElement) as Boolean
                if (isRemovableStorage == removable) {
                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }


}