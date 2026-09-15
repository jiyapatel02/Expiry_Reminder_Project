package com.example.expiry_reminder_project.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expiry_reminder_project.DocumentDetailsActivity
import com.example.expiry_reminder_project.R
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.utils.DateUtils
import com.example.expiry_reminder_project.RenewalActivity

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

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_document,
                parent,
                false
            )

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
            DateUtils.getStatus(
                document.expiryDate
            )

        val days =
            DateUtils.getDaysRemaining(
                document.expiryDate
            )

        holder.status.text = status

        when (status) {

            "VALID" -> {

                holder.status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_valid
                    )
                )

                holder.status.setBackgroundResource(
                    R.drawable.bg_status_valid
                )

                holder.strip.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_valid
                    )
                )

                holder.days.text =
                    "Valid for $days days"

                holder.days.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_valid
                    )
                )
            }

            "EXPIRING SOON" -> {

                holder.status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_due_soon
                    )
                )

                holder.status.setBackgroundResource(
                    R.drawable.bg_status_soon
                )

                holder.strip.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_due_soon
                    )
                )

                holder.days.text =
                    if (days == 0L) {
                        "Expires today"
                    } else {
                        "Expires in $days days"
                    }

                holder.days.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_due_soon
                    )
                )
            }

            "EXPIRED" -> {

                holder.status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_expired
                    )
                )

                holder.status.setBackgroundResource(
                    R.drawable.bg_status_expired
                )

                holder.strip.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_expired
                    )
                )

                holder.days.text =
                    if (days == -1L) {
                        "Expired 1 day ago"
                    } else {
                        "Expired ${kotlin.math.abs(days)} days ago"
                    }

                holder.days.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.status_expired
                    )
                )
            }

            else -> {

                holder.status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.text_secondary
                    )
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
                    RenewalActivity::class.java
                )

            intent.putExtra(
                "document_id",
                document.id
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