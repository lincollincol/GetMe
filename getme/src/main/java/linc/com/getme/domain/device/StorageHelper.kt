package linc.com.getme.domain.device

interface StorageHelper {
    fun getExternalStoragePath(isRemovableStorage: Boolean): String?
}