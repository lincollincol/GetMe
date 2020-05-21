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

internal class FilesystemPresenterImpl(
    private val interactor: FilesystemInteractor,
    private val getMeInterfaceSettings: GetMeInterfaceSettings,
    private val compositeDisposable: CompositeDisposable
) : FilesystemPresenter {

    private var view: FilesystemView? = null

    /**
     * Bind created view and set first settings
     * */
    override fun bind(view: FilesystemView) {
        this.view = view
        view.initFilesystemEntitiesAdapter(
            getMeInterfaceSettings.adapterAnimation,
            getMeInterfaceSettings.animationFirstOnly
        )
    }

    /**
     * Clear resources when view is stopped
     * */
    override fun unbind() {
        this.view = null
        this.compositeDisposable.clear()
    }

    /**
     * Some widgets created and here pass other settings
     * */
    override fun prepare() {
        val selectionState = when(getMeInterfaceSettings.selectionType) {
            GetMeInterfaceSettings.SELECTION_SINGLE -> false
            else -> true
        }
        view?.enableSelection(
            selectionState,
            getMeInterfaceSettings.selectionMaxSize
        )
        view?.enableOverScroll(getMeInterfaceSettings.enableOverScroll)
    }

    override fun getFilesystemEntities() {
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

    override fun openFilesystemEntity(filesystemEntityModel: FilesystemEntityModel) {
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

    override fun openPreviousFilesystemEntity() {
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

    override fun handleFilesystemEntityAction(filesystemEntityModel: FilesystemEntityModel) {
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

    override fun prepareResultFiles(filesystemEntityModels: List<FilesystemEntityModel>) {
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

    override fun saveCurrentState() {
        interactor.saveState()
    }

    override fun restoreState() {
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

    override fun retrieveState() = interactor.retrieveState()

    private fun showEmptyDirectorySign(isDirectoryEmpty: Boolean) {
        view?.showEmptySign(when(isDirectoryEmpty) {
            true -> View.VISIBLE
            else -> View.GONE
        })
    }

}