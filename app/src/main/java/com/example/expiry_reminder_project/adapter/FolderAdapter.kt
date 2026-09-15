package com.example.expiry_reminder_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.R
import com.example.expiry_reminder_project.model.Folder
import com.google.android.material.card.MaterialCardView

class FolderAdapter(
    private var folders: MutableList<Folder>,
    private val onFolderClick: (Folder) -> Unit,
    private val onEditClick: (Folder) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    class FolderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val folderCard: MaterialCardView =
            itemView.findViewById(R.id.folderCard)

        val tvFolderName: TextView =
            itemView.findViewById(R.id.tvFolderName)

        val tvFolderDescription: TextView =
            itemView.findViewById(
                R.id.tvFolderDescription
            )

        val tvEdit: TextView =
            itemView.findViewById(R.id.tvEdit)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_folder,
                    parent,
                    false
                )

        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FolderViewHolder,
        position: Int
    ) {

        val folder = folders[position]

        holder.tvFolderName.text =
            folder.name

        holder.tvFolderDescription.text =
            if (folder.description.isBlank()) {
                "No description"
            } else {
                folder.description
            }

        // Open folder
        holder.folderCard.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (
                currentPosition
                != RecyclerView.NO_POSITION
            ) {

                onFolderClick(
                    folders[currentPosition]
                )
            }
        }

        // Edit folder
        holder.tvEdit.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (
                currentPosition
                != RecyclerView.NO_POSITION
            ) {

                onEditClick(
                    folders[currentPosition]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    fun updateList(
        newList: MutableList<Folder>
    ) {

        folders = newList

        notifyDataSetChanged()
    }
}