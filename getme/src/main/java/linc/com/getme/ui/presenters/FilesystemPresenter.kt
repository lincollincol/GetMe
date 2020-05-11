package linc.com.getme.ui.presenters

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.StateManager


class FilesystemPresenter(
    private val interactor: FilesystemInteractor,
    private val stateManager: StateManager
) {

    private var view: FilesystemView? = null

    fun bind(view: FilesystemView) {
        this.view = view
    }

    fun unbind() {
        this.view = null
    }

    fun getFilesystemEntities() {
        interactor.getRoot()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showFilesystemEntities(it)
            }, {
                view?.showError(it.localizedMessage)
            })
    }

    fun openFilesystemEntity(filesystemEntity: FilesystemEntity) {
        interactor.openFilesystemEntity(filesystemEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                stateManager.goTo(filesystemEntity.path)
                println(filesystemEntity.path)
                view?.showFilesystemEntities(it)
            }, {
                view?.showError(it.localizedMessage)
            })
    }

    fun openPreviousFilesystemEntity() {
        stateManager.goBack()

//        val filesystemEntities: Single<List<FilesystemEntity>>
        val filesystemEntities = if(!stateManager.hasState() ) {
            view?.closeManager()
            return
        }else if(stateManager.getLast() == "root") {
            interactor.getRoot()
        }else {
            interactor.openFilesystemEntity(
                FilesystemEntity.fromPath(stateManager.getLast())
            )
        }

        filesystemEntities
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showFilesystemEntities(it)
            }, {
                view?.showError(it.localizedMessage)
            })
    }

}