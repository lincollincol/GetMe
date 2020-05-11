package linc.com.getme.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.ui.adapters.selection.FilesystemEntityDetails
import linc.com.getme.ui.adapters.selection.ViewHolderWithDetails

class FilesystemEntitiesAdapter : RecyclerView.Adapter<FilesystemEntitiesAdapter.FilesystemEntityViewHolder>() {

    private val filesystemEntities = mutableListOf<FilesystemEntity>()
    private lateinit var filesystemEntityClickListener: FilesystemEntityClickListener

    fun setFilesystemEntityClickListener(filesystemEntityClickListener: FilesystemEntityClickListener) {
        this.filesystemEntityClickListener = filesystemEntityClickListener
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
        holder.bind(filesystemEntities[position])
    }

    inner class FilesystemEntityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        ViewHolderWithDetails<FilesystemEntity> {

        fun bind(filesystemEntity: FilesystemEntity) {
            itemView.findViewById<TextView>(R.id.filesystemEntity).text = filesystemEntity.title
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