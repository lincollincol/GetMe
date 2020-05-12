package linc.com.getme.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.domain.models.FilesystemEntity
import linc.com.getme.ui.adapters.selection.FilesystemEntityDetails
import linc.com.getme.ui.adapters.selection.ViewHolderWithDetails
import linc.com.getme.utils.DateFormatUtil
import linc.com.getme.utils.SizeUtil

class FilesystemEntitiesAdapter : RecyclerView.Adapter<FilesystemEntitiesAdapter.FilesystemEntityViewHolder>() {

    private val filesystemEntities = mutableListOf<FilesystemEntity>()
    private lateinit var filesystemEntityClickListener: FilesystemEntityClickListener
    private lateinit var selectionTracker: SelectionTracker<FilesystemEntity>

    fun setFilesystemEntityClickListener(filesystemEntityClickListener: FilesystemEntityClickListener) {
        this.filesystemEntityClickListener = filesystemEntityClickListener
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<FilesystemEntity>) {
        this.selectionTracker = selectionTracker
    }

    fun updateFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        this.filesystemEntities.clear()
        this.filesystemEntities.addAll(filesystemEntities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilesystemEntityViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filesystem_entity, parent, false)
    )

    override fun getItemCount() = filesystemEntities.count()

    override fun onBindViewHolder(holder: FilesystemEntityViewHolder, position: Int) {
        val filesystemEntity = filesystemEntities[position]
        holder.bind(filesystemEntity, false/*selectionTracker.isSelected(filesystemEntity)*/)
    }

    inner class FilesystemEntityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        ViewHolderWithDetails<FilesystemEntity> {

        fun bind(filesystemEntity: FilesystemEntity, selected: Boolean) {
            itemView.findViewById<TextView>(R.id.filesystemEntityTitle).text = filesystemEntity.title

            itemView.findViewById<TextView>(R.id.filesystemEntityDetails).apply {
                visibility = if(filesystemEntity.isDirectory) View.GONE else View.VISIBLE
                text = "${SizeUtil.format(filesystemEntity.size)}, ${DateFormatUtil.formatFromLong(filesystemEntity.lastModified)}"
            }

            itemView.findViewById<ImageView>(R.id.filesystemEntityTypeIcon).apply {
                setImageResource(
                    if(filesystemEntity.isDirectory)
                        R.drawable.ic_folder
                    else
                        R.drawable.ic_file
                )
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            filesystemEntityClickListener.onClick(filesystemEntities[adapterPosition])
        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<FilesystemEntity> {
            return FilesystemEntityDetails(
                adapterPosition,
                filesystemEntities[adapterPosition]
            )
        }

    }

    interface FilesystemEntityClickListener {
        fun onClick(filesystemEntity: FilesystemEntity)
    }

}