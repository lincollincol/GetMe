package linc.com.getme.ui.presenters

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.mappers.FilesystemEntityMapper
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.views.FilesystemView
import java.io.File


internal class FilesystemPresenter(
    context: Context
) : ViewModel() {

    val viewModelData: MutableLiveData<ViewModelEvent> by lazy { MutableLiveData<ViewModelEvent>() }
    private var interactor: FilesystemInteractor = FilesystemInteractor(
        StorageHelper(context),
        StateManager()
    )
    private lateinit var getMeInterfaceSettings: GetMeInterfaceSettings

    init {
        println("BIND_____!!!!!")

    }

    fun bind(getMeFilesystemSettings: GetMeFilesystemSettings, getMeInterfaceSettings: GetMeInterfaceSettings) {
        interactor.getMeFilesystemSettings = getMeFilesystemSettings
        this.getMeInterfaceSettings = getMeInterfaceSettings
        val selectionState = when(getMeInterfaceSettings.selectionType) {
            GetMeInterfaceSettings.SELECTION_SINGLE -> false
            else -> true
        }

        viewModelData.postValue(ViewModelEvent.UpdateSettingsEvent(selectionState))
    }

    fun getFilesystemEntities() {
        interactor.execute()
            .map { FilesystemEntityMapper.toFilesystemModelsList(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModelData.postValue(ViewModelEvent.UpdateDataEvent(it))
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
                viewModelData.postValue(ViewModelEvent.UpdateDataEvent(it))
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
                    viewModelData.postValue(ViewModelEvent.CloseEvent(emptyList()))
                }else {
                    viewModelData.postValue(ViewModelEvent.UpdateDataEvent(it))
                }

            }, {
                it.printStackTrace()
            })
    }

    fun handleFilesystemEntityAction(filesystemEntityModel: FilesystemEntityModel) {
        println("FILE_ENTITY==========${filesystemEntityModel.title}")
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
                viewModelData.postValue(ViewModelEvent.CloseEvent(it))
            }, {
                it.printStackTrace()
            })
    }

}