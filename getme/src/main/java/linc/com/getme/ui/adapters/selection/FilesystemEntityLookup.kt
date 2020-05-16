package linc.com.getme.ui.adapters.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel

internal class FilesystemEntityLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<FilesystemEntityModel>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<FilesystemEntityModel>? {
        return recyclerView.findChildViewUnder(e.x, e.y)?.let {
                (recyclerView.getChildViewHolder(it) as? ViewHolderWithDetails<FilesystemEntityModel>)?.getItemDetails()
            }
    }
}