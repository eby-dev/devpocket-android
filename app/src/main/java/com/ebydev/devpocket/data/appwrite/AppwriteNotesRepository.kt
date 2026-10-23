package com.ebydev.devpocket.data.appwrite

import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.Query
import io.appwrite.services.Account
import io.appwrite.services.TablesDB
import com.ebydev.devpocket.domain.model.Note
import com.ebydev.devpocket.domain.model.NoteCategory
import com.ebydev.devpocket.domain.model.NoteInput
import com.ebydev.devpocket.domain.repository.NotesRepository
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AppwriteNotesRepository(client: AppwriteClient, private val config: AppwriteConfig) : NotesRepository {
    private val tables = TablesDB(client.client)
    private val account = Account(client.client)

    override suspend fun listNotes(): Result<List<Note>> = runCatching {
        val response = tables.listRows(config.databaseId, config.tableId, listOf(Query.limit(100)))
        response.rows.map(::toNote).sortedByDescending(Note::updatedAt)
    }

    override suspend fun getNote(id: String): Result<Note> = runCatching {
        toNote(tables.getRow(config.databaseId, config.tableId, id))
    }

    override suspend fun createNote(input: NoteInput): Result<Note> = runCatching {
        val user = account.get()
        val now = timestamp()
        val row = tables.createRow(
            databaseId = config.databaseId,
            tableId = config.tableId,
            rowId = ID.unique(),
            data = input.toData(user.id, now),
            permissions = privatePermissions(user.id),
        )
        toNote(row)
    }

    override suspend fun updateNote(id: String, input: NoteInput): Result<Note> = runCatching {
        val row = tables.updateRow(
            databaseId = config.databaseId,
            tableId = config.tableId,
            rowId = id,
            data = input.toData(ownerId = null, updatedAt = timestamp()),
        )
        toNote(row)
    }

    override suspend fun deleteNote(id: String): Result<Unit> = runCatching {
        tables.deleteRow(config.databaseId, config.tableId, id)
        Unit
    }

    private fun NoteInput.toData(ownerId: String?, updatedAt: String): Map<String, Any?> = buildMap {
        if (ownerId != null) put("ownerId", ownerId)
        put("title", title.trim())
        put("content", content.trim())
        put("category", category.label)
        put("tags", JSONArray(tags).toString())
        put("isFavorite", isFavorite)
        if (ownerId != null) put("createdAt", updatedAt)
        put("updatedAt", updatedAt)
    }

    private fun privatePermissions(userId: String) = listOf(
        Permission.read(Role.user(userId)),
        Permission.update(Role.user(userId)),
        Permission.delete(Role.user(userId)),
    )

    private fun toNote(row: io.appwrite.models.Row<Map<String, Any>>): Note {
        val data = row.data
        val tagList = runCatching {
            val array = JSONArray(data["tags"]?.toString().orEmpty())
            List(array.length()) { array.optString(it) }.filter(String::isNotBlank)
        }.getOrDefault(emptyList())
        return Note(
            id = row.id,
            ownerId = data["ownerId"]?.toString().orEmpty(),
            title = data["title"]?.toString().orEmpty(),
            content = data["content"]?.toString().orEmpty(),
            category = NoteCategory.from(data["category"]?.toString().orEmpty()),
            tags = tagList,
            isFavorite = data["isFavorite"] as? Boolean ?: false,
            createdAt = data["createdAt"]?.toString() ?: row.createdAt,
            updatedAt = data["updatedAt"]?.toString() ?: row.updatedAt,
        )
    }

    private fun timestamp(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(System.currentTimeMillis())
}
