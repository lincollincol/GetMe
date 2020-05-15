package linc.com.getme.ui.presenters

import linc.com.getme.ui.models.FilesystemEntityModel
import java.io.File

sealed class ViewModelEvent {
    data class UpdateDataEvent(val data: List<FilesystemEntityModel>) : ViewModelEvent()
    data class CloseEvent(val data: List<File>) : ViewModelEvent()
    data class UpdateSettingsEvent(val selectionEnable: Boolean) : ViewModelEvent()
}