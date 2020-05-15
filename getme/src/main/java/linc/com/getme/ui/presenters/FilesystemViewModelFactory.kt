package linc.com.getme.ui.presenters

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.ui.GetMeInterfaceSettings

internal class FilesystemViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilesystemPresenter(context) as T
    }

}