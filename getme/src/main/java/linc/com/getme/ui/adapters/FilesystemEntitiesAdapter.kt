package linc.com.getme.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.adapters.selection.FilesystemEntityDetails
import linc.com.getme.ui.adapters.selection.ViewHolderWithDetails
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.utils.DateFormatUtil
import linc.com.getme.utils.SizeUtil

class FilesystemEntitiesAdapter : RecyclerView.Adapter<FilesystemEntitiesAdapter.FilesystemEntityViewHolder>() {

    private val filesystemEntityModels = mutableListOf<FilesystemEntityModel>()

    private lateinit var filesystemEntityClickListener: FilesystemEntityClickListener
    private var selectionTracker: SelectionTracker<FilesystemEntityModel>? = null

    fun setFilesystemEntityClickListener(filesystemEntityClickListener: FilesystemEntityClickListener) {
        this.filesystemEntityClickListener = filesystemEntityClickListener
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<FilesystemEntityModel>) {
        this.selectionTracker = selectionTracker
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
            .inflate(R.layout.item_filesystem_entity, parent, false)
    )

    override fun getItemCount() = filesystemEntityModels.count()

    override fun onBindViewHolder(holder: FilesystemEntityViewHolder, position: Int) {
        val filesystemEntity = filesystemEntityModels[position]
        holder.bind(
            filesystemEntity,
            selectionTracker?.isSelected(filesystemEntity) ?: false
        )
    }

    inner class FilesystemEntityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        ViewHolderWithDetails<FilesystemEntityModel> {

        private var selected: Boolean = false

        fun bind(filesystemEntityModel: FilesystemEntityModel, selected: Boolean) {

            this.selected = selected

            itemView.findViewById<ConstraintLayout>(R.id.filesystemEntityLayout).isSelected = selected

            itemView.findViewById<TextView>(R.id.filesystemEntityTitle).text = filesystemEntityModel.title

            itemView.findViewById<ImageView>(R.id.iconSelected).apply {
                visibility = if(selected) View.VISIBLE else View.GONE
            }

            itemView.findViewById<TextView>(R.id.filesystemEntityDetails).apply {
                val details = when(filesystemEntityModel.isDirectory) {
                    true -> "Directory"
                    else -> "${filesystemEntityModel.size}, ${filesystemEntityModel.lastModified}"
                }
                text = details
            }

            itemView.findViewById<ImageView>(R.id.filesystemEntityTypeIcon).apply {
                setImageResource(
                    if(filesystemEntityModel.isDirectory)
                        R.drawable.ic_folder
                    else
                        R.drawable.ic_file
                )
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(!selected)
                filesystemEntityClickListener.onClick(filesystemEntityModels[adapterPosition])
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