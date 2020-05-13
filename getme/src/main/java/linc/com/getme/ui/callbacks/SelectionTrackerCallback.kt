package linc.com.getme.ui.callbacks

import androidx.recyclerview.selection.SelectionTracker
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel

interface SelectionTrackerCallback {
    fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntityModel>)
}