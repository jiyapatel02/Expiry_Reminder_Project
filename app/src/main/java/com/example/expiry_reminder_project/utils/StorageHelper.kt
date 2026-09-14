package com.example.expiry_reminder_project.utils

import android.content.Context
import com.example.expiry_reminder_project.model.Document
import com.example.expiry_reminder_project.model.Folder
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object StorageHelper {

    private const val PREF_NAME = "ExpiryReminder"

    private const val DOCUMENTS_KEY = "documents"
    private const val FOLDERS_KEY = "folders"

    // ---------------------------------------------------------
    // DOCUMENTS
    // ---------------------------------------------------------

    fun getDocuments(context: Context): MutableList<Document> {

        val preferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val jsonString =
            preferences.getString(DOCUMENTS_KEY, "[]") ?: "[]"

        val documents = mutableListOf<Document>()

        try {

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)

                documents.add(
                    Document(
                        id = jsonObject.optString("id"),
                        name = jsonObject.optString("name"),
                        type = jsonObject.optString("type"),
                        documentNumber =
                            jsonObject.optString("documentNumber"),
                        issueDate =
                            jsonObject.optString("issueDate"),
                        expiryDate =
                            jsonObject.optString("expiryDate"),
                        folderId =
                            jsonObject.optString("folderId"),
                        notes =
                            jsonObject.optString("notes")
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return documents
    }

    fun addDocument(
        context: Context,
        document: Document
    ) {

        val documents = getDocuments(context)

        documents.add(document)

        saveDocuments(context, documents)
    }

    fun updateDocument(
        context: Context,
        updatedDocument: Document
    ) {

        val documents = getDocuments(context)

        val index =
            documents.indexOfFirst {
                it.id == updatedDocument.id
            }

        if (index != -1) {

            documents[index] = updatedDocument

            saveDocuments(context, documents)
        }
    }

    fun deleteDocument(
        context: Context,
        documentId: String
    ) {

        val documents = getDocuments(context)

        documents.removeAll {
            it.id == documentId
        }

        saveDocuments(context, documents)
    }

    private fun saveDocuments(
        context: Context,
        documents: List<Document>
    ) {

        val jsonArray = JSONArray()

        documents.forEach { document ->

            val jsonObject = JSONObject()

            jsonObject.put("id", document.id)
            jsonObject.put("name", document.name)
            jsonObject.put("type", document.type)
            jsonObject.put(
                "documentNumber",
                document.documentNumber
            )
            jsonObject.put(
                "issueDate",
                document.issueDate
            )
            jsonObject.put(
                "expiryDate",
                document.expiryDate
            )
            jsonObject.put(
                "folderId",
                document.folderId
            )
            jsonObject.put(
                "notes",
                document.notes
            )

            jsonArray.put(jsonObject)
        }

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                DOCUMENTS_KEY,
                jsonArray.toString()
            )
            .apply()
    }

    // ---------------------------------------------------------
    // FOLDERS
    // ---------------------------------------------------------

    fun getFolders(context: Context): MutableList<Folder> {

        val preferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val jsonString =
            preferences.getString(FOLDERS_KEY, "[]") ?: "[]"

        val folders = mutableListOf<Folder>()

        try {

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)

                folders.add(
                    Folder(
                        id = jsonObject.optString("id"),
                        name = jsonObject.optString("name"),
                        description =
                            jsonObject.optString("description")
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return folders
    }

    fun addFolder(
        context: Context,
        folder: Folder
    ) {

        val folders = getFolders(context)

        folders.add(folder)

        saveFolders(context, folders)
    }

    fun updateFolder(
        context: Context,
        updatedFolder: Folder
    ) {

        val folders = getFolders(context)

        val index =
            folders.indexOfFirst {
                it.id == updatedFolder.id
            }

        if (index != -1) {

            folders[index] = updatedFolder

            saveFolders(context, folders)
        }
    }

    fun deleteFolder(
        context: Context,
        folderId: String
    ) {

        val folders = getFolders(context)

        folders.removeAll {
            it.id == folderId
        }

        saveFolders(context, folders)
    }

    private fun saveFolders(
        context: Context,
        folders: List<Folder>
    ) {

        val jsonArray = JSONArray()

        folders.forEach { folder ->

            val jsonObject = JSONObject()

            jsonObject.put("id", folder.id)
            jsonObject.put("name", folder.name)
            jsonObject.put(
                "description",
                folder.description
            )

            jsonArray.put(jsonObject)
        }

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                FOLDERS_KEY,
                jsonArray.toString()
            )
            .apply()
    }

    // ---------------------------------------------------------
    // ID GENERATION
    // ---------------------------------------------------------

    fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}