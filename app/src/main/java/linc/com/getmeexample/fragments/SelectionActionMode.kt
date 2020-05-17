package linc.com.getmeexample.fragments

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import linc.com.getme.ui.models.FilesystemEntityModel

class SelectionActionMode(
    private val selectionTracker: SelectionTracker<FilesystemEntityModel>,
    private val menuItemClick: MenuItemClickListener
) : ActionMode.Callback {

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        menuItemClick.onMenuItemClicked(item)
        mode?.finish()
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu?.add("GET")
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onDestroyActionMode(mode: ActionMode?) {
        selectionTracker.clearSelection()
    }

    interface MenuItemClickListener {
        fun onMenuItemClicked(item: MenuItem?)
    }
}