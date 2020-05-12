package linc.com.getme.ui.callbacks

import androidx.recyclerview.selection.SelectionTracker
import linc.com.getme.domain.models.FilesystemEntity

interface SelectionTrackerCallback {
    fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntity>)
}