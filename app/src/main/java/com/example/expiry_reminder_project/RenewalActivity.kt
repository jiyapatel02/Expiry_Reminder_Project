package com.example.expiry_reminder_project

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray

class RenewalActivity : AppCompatActivity() {

    private var documentId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_renewal)

        documentId =
            intent.getStringExtra("document_id") ?: ""

        setupBackButton()

        loadDocument()
    }

    private fun setupBackButton() {

        findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener {

                onBackPressedDispatcher.onBackPressed()
            }
    }

    private fun loadDocument() {

        val documents = getDocuments()

        for (i in 0 until documents.length()) {

            val document =
                documents.getJSONObject(i)

            if (
                document.optString("id")
                == documentId
            ) {

                val documentName =
                    document.optString(
                        "name",
                        "Document"
                    )

                val expiryDate =
                    document.optString(
                        "expiryDate",
                        ""
                    )

                val status =
                    DateUtils.getStatus(
                        expiryDate
                    )

                findViewById<TextView>(
                    R.id.tvDocumentName
                ).text = documentName

                findViewById<TextView>(
                    R.id.tvStatus
                ).text = status

                updateStatusAppearance(status)

                return
            }
        }

        // No specific document selected
        findViewById<TextView>(
            R.id.tvDocumentName
        ).text = "Document"

        findViewById<TextView>(
            R.id.tvStatus
        ).text = "Renewal Guidance"

        findViewById<TextView>(
            R.id.tvStatus
        ).setTextColor(
            getColor(R.color.primary_teal)
        )
    }

    private fun updateStatusAppearance(status: String) {

        val statusView =
            findViewById<TextView>(R.id.tvStatus)

        when (status.uppercase()) {

            "VALID" -> {
                statusView.setTextColor(
                    getColor(R.color.status_valid)
                )
            }

            "EXPIRING SOON" -> {
                statusView.setTextColor(
                    getColor(R.color.status_due_soon)
                )
            }

            "EXPIRED" -> {
                statusView.setTextColor(
                    getColor(R.color.status_expired)
                )
            }

            else -> {
                statusView.setTextColor(
                    getColor(R.color.text_secondary)
                )
            }
        }
    }

    private fun getDocuments(): JSONArray {

        val preferences =
            getSharedPreferences(
                "DocumentStorage",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "documents",
                "[]"
            )

        return try {

            JSONArray(data)

        } catch (e: Exception) {

            JSONArray()
        }
    }
}