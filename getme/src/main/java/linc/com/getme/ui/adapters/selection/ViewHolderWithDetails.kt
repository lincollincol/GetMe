package linc.com.getme.ui.adapters.selection

import androidx.recyclerview.selection.ItemDetailsLookup

interface ViewHolderWithDetails <T> {
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<T>
}