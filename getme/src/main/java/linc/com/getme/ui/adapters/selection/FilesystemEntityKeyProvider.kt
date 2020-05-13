package linc.com.getme.ui.adapters.selection

import androidx.recyclerview.selection.ItemKeyProvider
import linc.com.getme.ui.models.FilesystemEntityModel

class FilesystemEntityKeyProvider : ItemKeyProvider<FilesystemEntityModel>(SCOPE_CACHED) {
    private val filesystemEntityModel = mutableListOf<FilesystemEntityModel>()

    override fun getKey(position: Int): FilesystemEntityModel? = filesystemEntityModel[position]
    override fun getPosition(key: FilesystemEntityModel): Int = filesystemEntityModel.indexOf(key)

    fun setFilesystemEntities(filesystemEntityModel: List<FilesystemEntityModel>) {
        this.filesystemEntityModel.clear()
        this.filesystemEntityModel.addAll(filesystemEntityModel)
    }
}