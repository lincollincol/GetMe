package linc.com.getme.ui.presenters

import android.content.Context
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.mappers.FilesystemEntityMapper
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.views.FilesystemView


internal class FilesystemPresenter(
    private val interactor: FilesystemInteractor,
    private val getMeInterfaceSettings: GetMeInterfaceSettings
) {

    private var view: FilesystemView? = null

    fun bind(view: FilesystemView) {
        this.view = view
        val selectionState = when(getMeInterfaceSettings.selectionType) {
            GetMeInterfaceSettings.SELECTION_SINGLE -> false
            else -> true
        }
        view.enableSelection(selectionState)
    }

    fun unbind() {
        this.view = null
    }

    fun getFilesystemEntities() {
        interactor.execute()
            .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showFilesystemEntities(it)
            }, {
                it.printStackTrace()
            })
    }

    fun openFilesystemEntity(filesystemEntityModel: FilesystemEntityModel) {
        interactor.openFilesystemEntity(FilesystemEntityMapper.toFilesystemEntity(filesystemEntityModel))
            .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showFilesystemEntities(it)
            }, {
                it.printStackTrace()
            })
    }

    fun openPreviousFilesystemEntity() {
        interactor.openPreviousFilesystemEntity()
            .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                println("IS EMPTY ==== ${it.isEmpty()}")
                if(it.isEmpty()) {
                    view?.closeManager(emptyList())
                }else {
                    view?.showFilesystemEntities(it)
                    view?.scrollToTop()
                }

            }, {
                it.printStackTrace()
            })
    }

    fun handleFilesystemEntityAction(filesystemEntityModel: FilesystemEntityModel) {
        when(getMeInterfaceSettings.selectionType) {
            GetMeInterfaceSettings.SELECTION_MULTIPLE -> openFilesystemEntity(filesystemEntityModel)
            else -> {
                if(filesystemEntityModel.isDirectory)
                    openFilesystemEntity(filesystemEntityModel)
                else
                    prepareResultFiles(listOf(filesystemEntityModel))
            }
        }

    }

    fun prepareResultFiles(filesystemEntityModels: List<FilesystemEntityModel>) {

        val resultSingle = when(getMeInterfaceSettings.actionType) {
            GetMeFilesystemSettings.ACTION_SELECT_DIRECTORY -> interactor.prepareResultDirectory()
            else -> interactor.prepareResultFiles(
                        FilesystemEntityMapper.toFilesystemEntitiesList(filesystemEntityModels)
                    )
        }

        resultSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.closeManager(it)
            }, {
                it.printStackTrace()
            })
    }

    fun saveCurrentState() {
        interactor.saveState()
    }

    fun restoreState() {
        interactor.restoreState()
            .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showFilesystemEntities(it)
            }, {
                it.printStackTrace()
            })
    }
}