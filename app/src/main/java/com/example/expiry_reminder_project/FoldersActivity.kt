package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class FoldersActivity : AppCompatActivity() {

    private lateinit var folderContainer: ConstraintLayout
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_folders)

        folderContainer =
            findViewById(R.id.folderContainer)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<MaterialCardView>(
            R.id.btnAddFolder
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddFolderActivity::class.java
                )
            )
        }

        loadFolders()
    }

    override fun onResume() {
        super.onResume()

        if (::folderContainer.isInitialized) {
            loadFolders()
        }
    }

    private fun loadFolders() {

        folderContainer.removeAllViews()

        val emptyView =
            TextView(this)

        emptyView.id =
            View.generateViewId()

        emptyView.text =
            "No folders created yet"

        emptyView.gravity =
            android.view.Gravity.CENTER

        emptyView.setTextColor(
            getColor(R.color.text_secondary)
        )

        emptyView.textSize = 15f

        emptyView.setPadding(
            20,
            40,
            20,
            40
        )

        folderContainer.addView(
            emptyView
        )

        val preferences =
            getSharedPreferences(
                "ExpiryReminder",
                Context.MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "folders",
                "[]"
            ) ?: "[]"

        val folders =
            try {
                JSONArray(data)
            } catch (e: Exception) {
                JSONArray()
            }

        if (folders.length() == 0) {
            return
        }

        folderContainer.removeAllViews()

        var previousId: Int? = null

        for (i in 0 until folders.length()) {

            val folder =
                folders.getJSONObject(i)

            val folderId =
                folder.optString("id")

            val folderName =
                folder.optString(
                    "name",
                    folder.optString(
                        "folderName",
                        "Folder"
                    )
                )

            val description =
                folder.optString(
                    "description",
                    ""
                )

            val card =
                MaterialCardView(this)

            card.id =
                View.generateViewId()

            card.layoutParams =
                ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    105.dp()
                )

            card.radius =
                18.dp().toFloat()

            card.cardElevation =
                2.dp().toFloat()

            card.setCardBackgroundColor(
                getColor(
                    R.color.card_white
                )
            )

            card.strokeWidth =
                1.dp()

            card.strokeColor =
                getColor(
                    R.color.border
                )

            card.isClickable = true
            card.isFocusable = true

            val inner =
                ConstraintLayout(this)

            inner.layoutParams =
                ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )

            val icon =
                TextView(this)

            icon.id =
                View.generateViewId()

            icon.text = "📁"
            icon.textSize = 25f
            icon.gravity =
                android.view.Gravity.CENTER

            icon.setBackgroundResource(
                R.drawable.bg_avatar
            )

            val title =
                TextView(this)

            title.id =
                View.generateViewId()

            title.text =
                folderName

            title.setTextColor(
                getColor(
                    R.color.text_primary
                )
            )

            title.textSize = 16f
            title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            val sub =
                TextView(this)

            sub.id =
                View.generateViewId()

            sub.text =
                if (description.isBlank()) {
                    "Tap to view documents"
                } else {
                    description
                }

            sub.setTextColor(
                getColor(
                    R.color.text_secondary
                )
            )

            sub.textSize = 13f

            inner.addView(icon)
            inner.addView(title)
            inner.addView(sub)

            val set =
                ConstraintSet()

            set.clone(inner)

            set.constrainWidth(
                icon.id,
                52.dp()
            )

            set.constrainHeight(
                icon.id,
                52.dp()
            )

            set.connect(
                icon.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                16.dp()
            )

            set.connect(
                icon.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                16.dp()
            )

            set.connect(
                title.id,
                ConstraintSet.START,
                icon.id,
                ConstraintSet.END,
                14.dp()
            )

            set.connect(
                title.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                20.dp()
            )

            set.connect(
                title.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                16.dp()
            )

            set.connect(
                sub.id,
                ConstraintSet.START,
                icon.id,
                ConstraintSet.END,
                14.dp()
            )

            set.connect(
                sub.id,
                ConstraintSet.TOP,
                title.id,
                ConstraintSet.BOTTOM,
                4.dp()
            )

            set.connect(
                sub.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                16.dp()
            )

            set.applyTo(inner)

            card.addView(inner)

            folderContainer.addView(card)

            val params =
                card.layoutParams
                        as ConstraintLayout.LayoutParams

            params.width =
                ConstraintLayout.LayoutParams.MATCH_PARENT

            params.height =
                105.dp()

            params.topMargin =
                if (previousId == null) {
                    0
                } else {
                    12.dp()
                }

            if (previousId == null) {

                params.topToTop =
                    ConstraintSet.PARENT_ID

            } else {

                params.topToBottom =
                    previousId!!
            }

            params.startToStart =
                ConstraintSet.PARENT_ID

            params.endToEnd =
                ConstraintSet.PARENT_ID

            card.layoutParams =
                params

            previousId =
                card.id

            card.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        FolderDocumentsActivity::class.java
                    )

                intent.putExtra(
                    "folder_id",
                    folderId
                )

                startActivity(intent)
            }
        }
    }

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}