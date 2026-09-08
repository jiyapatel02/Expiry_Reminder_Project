package com.example.expiry_reminder_project

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class DocumentsActivity : AppCompatActivity() {

    private lateinit var documentsContainer: ConstraintLayout
    private lateinit var tvDocumentCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        documentsContainer = findViewById(R.id.documentsContainer)
        tvDocumentCount = findViewById(R.id.tvDocumentCount)
    }

    override fun onResume() {
        super.onResume()
        loadDocuments()
    }

    private fun loadDocuments() {

        documentsContainer.removeAllViews()

        val preferences =
            getSharedPreferences("ExpiryReminder", MODE_PRIVATE)

        val savedDocuments =
            preferences.getString("documents", "[]")

        val documents = JSONArray(savedDocuments)

        tvDocumentCount.text =
            "${documents.length()} document(s) saved"

        if (documents.length() == 0) {

            val emptyText = TextView(this)

            emptyText.id = View.generateViewId()
            emptyText.text = "📁\n\nNo documents added yet"
            emptyText.textSize = 18f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTextColor(Color.GRAY)

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                250
            )

            emptyText.layoutParams = params

            documentsContainer.addView(emptyText)

            return
        }

        for (i in 0 until documents.length()) {

            val document = documents.getJSONObject(i)

            createDocumentCard(
                document.getString("name"),
                document.getString("category"),
                document.getString("issueDate"),
                document.getString("expiryDate"),
                document.getString("notes")
            )
        }
    }

    private fun createDocumentCard(
        name: String,
        category: String,
        issueDate: String,
        expiryDate: String,
        notes: String
    ) {

        val card = CardView(this)

        card.radius = 20f
        card.cardElevation = 5f
        card.setContentPadding(20, 20, 20, 20)

        val cardLayout = ConstraintLayout(this)

        cardLayout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            180
        )

        val title = TextView(this)

        title.id = View.generateViewId()
        title.text = name
        title.textSize = 20f
        title.setTextColor(Color.BLACK)
        title.setTypeface(null, android.graphics.Typeface.BOLD)

        val details = TextView(this)

        details.id = View.generateViewId()

        details.text =
            "Category: $category\n" +
                    "Issue Date: $issueDate\n" +
                    "Expiry Date: $expiryDate\n" +
                    if (notes.isNotEmpty()) {
                        "Notes: $notes"
                    } else {
                        ""
                    }

        details.textSize = 14f
        details.setTextColor(Color.DKGRAY)

        cardLayout.addView(title)
        cardLayout.addView(details)

        val titleParams =
            title.layoutParams as ConstraintLayout.LayoutParams

        titleParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        titleParams.topToTop =
            ConstraintLayout.LayoutParams.PARENT_ID

        title.layoutParams = titleParams

        val detailsParams =
            details.layoutParams as ConstraintLayout.LayoutParams

        detailsParams.startToStart =
            ConstraintLayout.LayoutParams.PARENT_ID

        detailsParams.topToBottom = title.id

        detailsParams.topMargin = 10

        details.layoutParams = detailsParams

        card.addView(cardLayout)

        val cardParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        cardParams.bottomMargin = 15

        card.layoutParams = cardParams

        documentsContainer.addView(card)
    }
}