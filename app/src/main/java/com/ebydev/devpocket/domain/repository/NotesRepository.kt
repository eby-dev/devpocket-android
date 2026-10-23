package com.ebydev.devpocket.domain.repository

import com.ebydev.devpocket.domain.model.Note
import com.ebydev.devpocket.domain.model.NoteInput

interface NotesRepository {
    suspend fun listNotes(): Result<List<Note>>
    suspend fun getNote(id: String): Result<Note>
    suspend fun createNote(input: NoteInput): Result<Note>
    suspend fun updateNote(id: String, input: NoteInput): Result<Note>
    suspend fun deleteNote(id: String): Result<Unit>
}
