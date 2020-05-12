package linc.com.getme.ui.adapters.selection

import androidx.recyclerview.selection.ItemKeyProvider
import linc.com.getme.domain.models.FilesystemEntity

class FilesystemEntityKeyProvider : ItemKeyProvider<FilesystemEntity>(SCOPE_CACHED) {
    private val filesystemEntities = mutableListOf<FilesystemEntity>()
    override fun getKey(position: Int): FilesystemEntity? = filesystemEntities[position]
    override fun getPosition(key: FilesystemEntity): Int = filesystemEntities.indexOf(key)

    fun setFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        this.filesystemEntities.clear()
        this.filesystemEntities.addAll(filesystemEntities)
    }
}