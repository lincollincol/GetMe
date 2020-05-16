package linc.com.getme.ui.adapters.selection

import androidx.recyclerview.selection.ItemDetailsLookup
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel

internal class FilesystemEntityDetails(
    private val position: Int,
    private val selectionKey: FilesystemEntityModel
) : ItemDetailsLookup.ItemDetails<FilesystemEntityModel>() {
    override fun getSelectionKey(): FilesystemEntityModel? = selectionKey
    override fun getPosition(): Int = position
}