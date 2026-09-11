package com.example.expiry_reminder_project

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentsActivity : AppCompatActivity() {

    private lateinit var documentsContainer: ConstraintLayout
    private lateinit var tvDocumentCount: TextView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var btnBack: TextView

    private val displayFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_documents)

        documentsContainer =
            findViewById(R.id.documentsContainer)

        tvDocumentCount =
            findViewById(R.id.tvDocumentCount)

        tvEmptyMessage =
            findViewById(R.id.tvEmptyMessage)

        btnBack =
            findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        loadDocuments()
    }

    override fun onResume() {
        super.onResume()

        if (::documentsContainer.isInitialized) {
            loadDocuments()
        }
    }

    private fun loadDocuments() {

        if (documentsContainer.childCount > 1) {
            documentsContainer.removeViews(
                1,
                documentsContainer.childCount - 1
            )
        }

        val preferences = getSharedPreferences(
            "DocumentStorage",
            MODE_PRIVATE
        )

        val jsonString = preferences.getString(
            "documents",
            "[]"
        )

        val documents = JSONArray(jsonString)

        tvDocumentCount.text =
            if (documents.length() == 1) {
                "1 Document"
            } else {
                "${documents.length()} Documents"
            }

        if (documents.length() == 0) {
            tvEmptyMessage.visibility = View.VISIBLE
            return
        }

        tvEmptyMessage.visibility = View.GONE

        var previousId = R.id.tvDocumentCount

        for (i in 0 until documents.length()) {

            val document = documents.getJSONObject(i)

            val card = createDocumentCard(
                document,
                i
            )

            documentsContainer.addView(card)

            val params =
                card.layoutParams as ConstraintLayout.LayoutParams

            params.width = 0
            params.height =
                ConstraintLayout.LayoutParams.WRAP_CONTENT

            params.topToBottom = previousId
            params.startToStart =
                ConstraintLayout.LayoutParams.PARENT_ID
            params.endToEnd =
                ConstraintLayout.LayoutParams.PARENT_ID

            params.setMargins(
                0,
                if (i == 0) 8 else 12,
                0,
                0
            )

            card.layoutParams = params

            previousId = card.id
        }
    }

    private fun createDocumentCard(
        document: org.json.JSONObject,
        index: Int
    ): MaterialCardView {

        val card = MaterialCardView(this)

        card.id = View.generateViewId()

        card.radius = 18f
        card.cardElevation = 3f
        card.setCardBackgroundColor(
            Color.WHITE
        )

        card.strokeWidth = 1
        card.strokeColor =
            Color.parseColor("#E2E9E7")

        val cardLayout =
            ConstraintLayout(this)

        cardLayout.layoutParams =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

        cardLayout.setPadding(
            18,
            18,
            18,
            18
        )

        card.addView(cardLayout)

        val nameText = TextView(this)

        nameText.id = View.generateViewId()

        nameText.text =
            document.optString(
                "name",
                "Unnamed Document"
            )

        nameText.setTextColor(
            Color.parseColor("#17201F")
        )

        nameText.textSize = 18f
        nameText.setTypeface(null, android.graphics.Typeface.BOLD)

        cardLayout.addView(nameText)

        val nameParams =
            nameText.layoutParams as ConstraintLayout.LayoutParams

        nameParams.width = 0
        nameParams.height =
            ConstraintLayout.LayoutParams.WRAP_CONTENT

        nameParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        nameParams.topToTop =
            ConstraintLayout.LayoutParams.PARENT_ID

        nameParams.endToEnd =
            ConstraintLayout.LayoutParams.PARENT_ID

        nameText.layoutParams = nameParams

        val categoryText = TextView(this)

        categoryText.id = View.generateViewId()

        categoryText.text =
            document.optString(
                "category",
                "Other"
            )

        categoryText.setTextColor(
            Color.parseColor("#087F73")
        )

        categoryText.textSize = 14f

        cardLayout.addView(categoryText)

        val categoryParams =
            categoryText.layoutParams
                    as ConstraintLayout.LayoutParams

        categoryParams.width = 0
        categoryParams.height =
            ConstraintLayout.LayoutParams.WRAP_CONTENT

        categoryParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        categoryParams.topToBottom =
            nameText.id

        categoryParams.endToEnd =
            ConstraintLayout.LayoutParams.PARENT_ID

        categoryParams.topMargin = 6

        categoryText.layoutParams = categoryParams

        val expiryText = TextView(this)

        expiryText.id = View.generateViewId()

        val expiryMillis =
            document.optLong(
                "expiryDate",
                0
            )

        expiryText.text =
            "Expires: ${
                displayFormat.format(
                    Date(expiryMillis)
                )
            }"

        expiryText.textSize = 14f

        cardLayout.addView(expiryText)

        val expiryParams =
            expiryText.layoutParams
                    as ConstraintLayout.LayoutParams

        expiryParams.width = 0
        expiryParams.height =
            ConstraintLayout.LayoutParams.WRAP_CONTENT

        expiryParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        expiryParams.topToBottom =
            categoryText.id

        expiryParams.endToEnd =
            ConstraintLayout.LayoutParams.PARENT_ID

        expiryParams.topMargin = 12

        expiryText.layoutParams = expiryParams

        val statusText = TextView(this)

        statusText.id = View.generateViewId()

        val status = getDocumentStatus(
            expiryMillis
        )

        statusText.text = status.first

        statusText.textSize = 13f
        statusText.gravity = Gravity.CENTER

        statusText.setPadding(
            14,
            7,
            14,
            7
        )

        statusText.setTextColor(
            Color.parseColor(status.second)
        )

        statusText.setBackgroundColor(
            Color.parseColor(status.third)
        )

        cardLayout.addView(statusText)

        val statusParams =
            statusText.layoutParams
                    as ConstraintLayout.LayoutParams

        statusParams.width =
            ConstraintLayout.LayoutParams.WRAP_CONTENT

        statusParams.height =
            ConstraintLayout.LayoutParams.WRAP_CONTENT

        statusParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        statusParams.topToBottom =
            expiryText.id

        statusParams.bottomToBottom =
            ConstraintLayout.LayoutParams.PARENT_ID

        statusParams.topMargin = 12

        statusText.layoutParams = statusParams

        card.setOnClickListener {

            Toast.makeText(
                this,
                "Document details coming next",
                Toast.LENGTH_SHORT
            ).show()
        }

        return card
    }

    private fun getDocumentStatus(
        expiryMillis: Long
    ): Triple<String, String, String> {

        val currentTime =
            System.currentTimeMillis()

        val remainingDays =
            (expiryMillis - currentTime) /
                    (1000 * 60 * 60 * 24)

        return when {

            expiryMillis < currentTime -> {
                Triple(
                    "Expired",
                    "#D93636",
                    "#FDE7E7"
                )
            }

            remainingDays <= 30 -> {
                Triple(
                    "Due Soon",
                    "#D88900",
                    "#FFF3D9"
                )
            }

            else -> {
                Triple(
                    "Valid",
                    "#1B9A59",
                    "#E5F6ED"
                )
            }
        }
    }
}