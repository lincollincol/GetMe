package linc.com.getme.ui.adapters.selection

import androidx.recyclerview.selection.ItemDetailsLookup
import linc.com.getme.domain.FilesystemEntity

class FilesystemEntityDetails(
    private val position: Int,
    private val selectionKey: FilesystemEntity
) : ItemDetailsLookup.ItemDetails<FilesystemEntity>() {
    override fun getSelectionKey(): FilesystemEntity? = selectionKey
    override fun getPosition(): Int = position
}