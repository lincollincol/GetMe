package linc.com.getme.ui.presenters

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.StateManager


internal class FilesystemPresenter(
    private val interactor: FilesystemInteractor
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
                view?.showFilesystemEntities(it)
            }, {
                view?.showError(it.localizedMessage)
            })
    }

    fun openPreviousFilesystemEntity() {
        interactor.openPreviousFilesystemEntity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.isEmpty()) {
                    view?.closeManager()
                }else {
                    view?.showFilesystemEntities(it)
                }

            }, {
                view?.showError(it.message!!)
            })
    }

}