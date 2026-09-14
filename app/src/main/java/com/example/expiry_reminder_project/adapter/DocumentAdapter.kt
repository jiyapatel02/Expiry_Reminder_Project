package com.example.expiry_reminder_project.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.DocumentDetailsActivity
import com.example.expiry_reminder_project.R
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import android.content.Context
import android.content.Intent

class DocumentAdapter(
    private val context: Context,
    private var documents: MutableList<Document>
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView =
            itemView.findViewById(R.id.tvDocumentName)

        val number: TextView =
            itemView.findViewById(R.id.tvDocumentNumber)

        val expiry: TextView =
            itemView.findViewById(R.id.tvExpiry)

        val days: TextView =
            itemView.findViewById(R.id.tvDaysRemaining)

        val status: TextView =
            itemView.findViewById(R.id.tvStatus)

        val view: TextView =
            itemView.findViewById(R.id.tvView)

        val renew: TextView =
            itemView.findViewById(R.id.tvRenew)

        val strip: View =
            itemView.findViewById(R.id.statusStrip)

        val card: View =
            itemView.findViewById(R.id.documentCard)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)

        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DocumentViewHolder,
        position: Int
    ) {

        val document = documents[position]

        holder.name.text = document.name

        holder.number.text =
            if (document.documentNumber.isBlank()) {
                "Document number not added"
            } else {
                document.documentNumber
            }

        holder.expiry.text =
            "Expires: ${document.expiryDate}"

        val status =
            DateUtils.getStatus(document.expiryDate)

        val days =
            DateUtils.getDaysRemaining(document.expiryDate)

        holder.status.text = status

        when (status) {

            "VALID" -> {

                holder.status.setTextColor(
                    Color.parseColor("#16A36A")
                )

                holder.status.setBackgroundColor(
                    Color.parseColor("#E8F8F1")
                )

                holder.strip.setBackgroundColor(
                    Color.parseColor("#16A36A")
                )

                holder.days.text =
                    "Valid for $days days"

                holder.days.setTextColor(
                    Color.parseColor("#16A36A")
                )
            }

            "EXPIRING SOON" -> {

                holder.status.setTextColor(
                    Color.parseColor("#E99A00")
                )

                holder.status.setBackgroundColor(
                    Color.parseColor("#FFF5DF")
                )

                holder.strip.setBackgroundColor(
                    Color.parseColor("#E99A00")
                )

                holder.days.text =
                    if (days == 0L) {
                        "Expires today"
                    } else {
                        "Expires in $days days"
                    }

                holder.days.setTextColor(
                    Color.parseColor("#E99A00")
                )
            }

            "EXPIRED" -> {

                holder.status.setTextColor(
                    Color.parseColor("#D93636")
                )

                holder.status.setBackgroundColor(
                    Color.parseColor("#FDECEC")
                )

                holder.strip.setBackgroundColor(
                    Color.parseColor("#D93636")
                )

                holder.days.text =
                    if (days == -1L) {
                        "Expired 1 day ago"
                    } else {
                        "Expired ${kotlin.math.abs(days)} days ago"
                    }

                holder.days.setTextColor(
                    Color.parseColor("#D93636")
                )
            }

            else -> {

                holder.status.setTextColor(
                    Color.DKGRAY
                )

                holder.days.text =
                    "Invalid expiry date"
            }
        }

        val openDetails = {

            val intent =
                Intent(
                    context,
                    DocumentDetailsActivity::class.java
                )

            intent.putExtra(
                "document_id",
                document.id
            )

            context.startActivity(intent)
        }

        holder.card.setOnClickListener {
            openDetails()
        }

        holder.view.setOnClickListener {
            openDetails()
        }

        holder.renew.setOnClickListener {

            val intent =
                Intent(
                    context,
                    DocumentDetailsActivity::class.java
                )

            intent.putExtra(
                "document_id",
                document.id
            )

            intent.putExtra(
                "show_renewal",
                true
            )

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    fun updateList(
        newDocuments: MutableList<Document>
    ) {

        documents = newDocuments

        notifyDataSetChanged()
    }
}