package linc.com.getme.ui.adapters.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.domain.FilesystemEntity

class FilesystemEntityLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<FilesystemEntity>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<FilesystemEntity>? {
        return recyclerView.findChildViewUnder(e.x, e.y)?.let {
                (recyclerView.getChildViewHolder(it) as? ViewHolderWithDetails<FilesystemEntity>)?.getItemDetails()
            }
    }
}