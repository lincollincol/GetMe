package linc.com.getme.ui.presenters

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.mappers.FilesystemEntityMapper
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.views.FilesystemView

internal interface FilesystemPresenter {

    fun bind(view: FilesystemView)
    fun unbind()
    fun prepare()

    fun getFilesystemEntities()
    fun openFilesystemEntity(filesystemEntityModel: FilesystemEntityModel)
    fun openPreviousFilesystemEntity()
    fun handleFilesystemEntityAction(filesystemEntityModel: FilesystemEntityModel)
    fun prepareResultFiles(filesystemEntityModels: List<FilesystemEntityModel>)

    fun saveCurrentState()
    fun restoreState()

    fun retrieveState(): String
}