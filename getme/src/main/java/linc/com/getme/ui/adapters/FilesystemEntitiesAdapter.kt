package linc.com.getme.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.ui.adapters.selection.FilesystemEntityDetails
import linc.com.getme.ui.adapters.selection.ViewHolderWithDetails
import linc.com.getme.ui.models.FilesystemEntityModel

internal class FilesystemEntitiesAdapter : RecyclerView.Adapter<FilesystemEntitiesAdapter.FilesystemEntityViewHolder>() {

    private val filesystemEntityModels = mutableListOf<FilesystemEntityModel>()

    private lateinit var filesystemEntityClickListener: FilesystemEntityClickListener
    private var selectionTracker: SelectionTracker<FilesystemEntityModel>? = null
    @LayoutRes private var itemLayout: Int = -1

    fun setFilesystemEntityClickListener(filesystemEntityClickListener: FilesystemEntityClickListener) {
        this.filesystemEntityClickListener = filesystemEntityClickListener
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<FilesystemEntityModel>) {
        this.selectionTracker = selectionTracker
    }

    fun setLayout(@LayoutRes itemLayout: Int) {
        this.itemLayout = itemLayout
    }

    fun updateFilesystemEntities(filesystemEntities: List<FilesystemEntityModel>) {
        this.filesystemEntityModels.apply {
            clear()
            addAll(filesystemEntities)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilesystemEntityViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(itemLayout, parent, false)
    )

    override fun getItemCount() = filesystemEntityModels.count()

    override fun onBindViewHolder(holder: FilesystemEntityViewHolder, position: Int) {
        val filesystemEntity = filesystemEntityModels[position]
        holder.bind(
            filesystemEntity,
            selectionTracker?.isSelected(filesystemEntity) ?: false,
            selectionTracker?.hasSelection() ?: false
        )
    }

    inner class FilesystemEntityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        ViewHolderWithDetails<FilesystemEntityModel> {

        private var selectionMode: Boolean = false

        fun bind(filesystemEntityModel: FilesystemEntityModel, selected: Boolean, selectionMode: Boolean) {
            this.selectionMode = selectionMode

            itemView.findViewById<ConstraintLayout>(R.id.fileLayoutGetMe).isSelected = selected

            itemView.findViewById<TextView>(R.id.fileTitleGetMe)?.text = filesystemEntityModel.title

            itemView.findViewById<ImageView>(R.id.fileSelectedIconGetMe)?.apply {
                visibility = if(selected) View.VISIBLE else View.GONE
            }

            itemView.findViewById<TextView>(R.id.fileDetailsGetMe)?.apply {
                val details = when(filesystemEntityModel.isDirectory) {
                    true -> "Directory"
                    else -> "${filesystemEntityModel.size}, ${filesystemEntityModel.lastModified}"
                }
                text = details
            }

            itemView.findViewById<ImageView>(R.id.fileTypeIconGetMe)?.apply {
                setImageResource(
                    if(filesystemEntityModel.isDirectory)
                        R.drawable.ic_directory_get_me
                    else
                        R.drawable.ic_file_get_me
                )
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if(!selectionMode) {
                when(itemView.id) {
                    v?.id -> filesystemEntityClickListener.onClick(filesystemEntityModels[adapterPosition])
                }
            }

        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<FilesystemEntityModel> {
            return FilesystemEntityDetails(
                adapterPosition,
                filesystemEntityModels[adapterPosition]
            )
        }

    }

    interface FilesystemEntityClickListener {
        fun onClick(filesystemEntityModel: FilesystemEntityModel)
    }

}



/*

inner class FilesystemEntityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        ViewHolderWithDetails<FilesystemEntityModel> {

        private var selectionMode: Boolean = false

        fun bind(filesystemEntityModel: FilesystemEntityModel, selected: Boolean, selectionMode: Boolean) {
            this.selectionMode = selectionMode

            itemView.findViewById<ConstraintLayout>(R.id.fileLayoutGetMe).isSelected = selected

            itemView.findViewById<TextView>(R.id.fileTitleGetMe).text = filesystemEntityModel.title

            itemView.findViewById<ImageView>(R.id.fileSelectedIconGetMe).apply {
                visibility = if(selected) View.VISIBLE else View.GONE
            }

            itemView.findViewById<TextView>(R.id.fileDetailsGetMe).apply {
                val details = when(filesystemEntityModel.isDirectory) {
                    true -> "Directory"
                    else -> "${filesystemEntityModel.size}, ${filesystemEntityModel.lastModified}"
                }
                text = details
            }

            itemView.findViewById<ImageView>(R.id.fileTypeIconGetMe).apply {
                setImageResource(
                    if(filesystemEntityModel.isDirectory)
                        R.drawable.ic_directory_get_me
                    else
                        R.drawable.ic_file_get_me
                )
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(!selectionMode)
                filesystemEntityClickListener.onClick(filesystemEntityModels[adapterPosition])
        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<FilesystemEntityModel> {
            return FilesystemEntityDetails(
                adapterPosition,
                filesystemEntityModels[adapterPosition]
            )
        }

    }

 */