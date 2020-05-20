package linc.com.getme.ui.presenters

import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.mappers.FilesystemEntityMapper
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.views.FilesystemView

internal class FilesystemPresenter(
    private val interactor: FilesystemInteractor,
    private val getMeInterfaceSettings: GetMeInterfaceSettings,
    private val compositeDisposable: CompositeDisposable
) {

    private var view: FilesystemView? = null

    /**
     * Bind created view and set first settings
     * */
    fun bind(view: FilesystemView) {
        this.view = view
        view.initFilesystemEntitiesAdapter(
            getMeInterfaceSettings.adapterAnimation,
            getMeInterfaceSettings.animationFirstOnly
        )
    }

    /**
     * Clear resources when view is stopped
     * */
    fun unbind() {
        this.view = null
        this.compositeDisposable.clear()
    }

    /**
     * Some widgets created and here pass other settings
     * */
    fun prepare() {
        val selectionState = when(getMeInterfaceSettings.selectionType) {
            GetMeInterfaceSettings.SELECTION_SINGLE -> false
            else -> true
        }
        view?.enableSelection(selectionState)
    }

    fun getFilesystemEntities() {
        compositeDisposable.add(
            interactor.execute()
                .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showEmptyDirectorySign(it.isEmpty())
                    view?.showFilesystemEntities(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun openFilesystemEntity(filesystemEntityModel: FilesystemEntityModel) {
        compositeDisposable.add(
            interactor.openFilesystemEntity(FilesystemEntityMapper.toFilesystemEntity(filesystemEntityModel))
                .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showEmptyDirectorySign(it.isEmpty())
                    view?.showFilesystemEntities(it)
                    view?.scrollToTop()
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun openPreviousFilesystemEntity() {
        compositeDisposable.add(
            interactor.openPreviousFilesystemEntity()
                .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if(it.isEmpty()) {
                        view?.closeManager(emptyList())
                    }else {
                        showEmptyDirectorySign(it.isEmpty())
                        view?.showFilesystemEntities(it)
                        view?.scrollToTop()
                    }
                }, {
                    it.printStackTrace()
                })
        )
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

        compositeDisposable.add(
            resultSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.closeManager(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun saveCurrentState() {
        interactor.saveState()
    }

    fun restoreState() {
        compositeDisposable.add(
            interactor.restoreState()
                .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showEmptyDirectorySign(it.isEmpty())
                    view?.showFilesystemEntities(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun retrieveState() = interactor.retrieveState()

    private fun showEmptyDirectorySign(isDirectoryEmpty: Boolean) {
        view?.showEmptySign(when(isDirectoryEmpty) {
            true -> View.VISIBLE
            else -> View.GONE
        })
    }
}