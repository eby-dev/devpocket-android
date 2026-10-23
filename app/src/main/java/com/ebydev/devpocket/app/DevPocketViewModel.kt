package com.ebydev.devpocket.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ebydev.devpocket.DevPocketApplication
import com.ebydev.devpocket.data.appwrite.SignedInUser
import com.ebydev.devpocket.domain.model.Note
import com.ebydev.devpocket.domain.model.NoteInput
import com.ebydev.devpocket.domain.model.validateNoteInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DevPocketUiState(
    val configured: Boolean = false,
    val user: SignedInUser? = null,
    val isRestoringSession: Boolean = false,
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val authError: String? = null,
    val search: String = "",
    val category: String? = null,
)

class DevPocketViewModel(application: Application) : AndroidViewModel(application) {
    private val graph = (application as DevPocketApplication).appGraph
    private val _state = MutableStateFlow(
        DevPocketUiState(configured = graph.config.isReady, isRestoringSession = graph.config.isReady),
    )
    val state = _state.asStateFlow()

    init {
        if (graph.config.isReady) {
            val authRepository = graph.auth
            if (authRepository == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRestoringSession = false,
                    authError = appwriteUnavailableError().message,
                )
            } else viewModelScope.launch {
                authRepository.currentUser().fold(
                    onSuccess = { user ->
                        _state.value = _state.value.copy(user = user, isLoading = false, isRestoringSession = false)
                        if (user != null) loadNotes()
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isRestoringSession = false,
                            authError = error.toUserMessage(),
                        )
                    },
                )
            }
        } else {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun login(email: String, password: String) = authenticate {
        graph.auth?.login(email, password) ?: Result.failure(appwriteUnavailableError())
    }

    fun register(name: String, email: String, password: String) = authenticate {
        graph.auth?.register(name, email, password) ?: Result.failure(appwriteUnavailableError())
    }

    private fun authenticate(request: suspend () -> Result<SignedInUser>) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, authError = null)
            request().fold(
                onSuccess = { user -> _state.value = _state.value.copy(user = user, isSaving = false); loadNotes() },
                onFailure = { error -> _state.value = _state.value.copy(isSaving = false, authError = error.toUserMessage()) },
            )
        }
    }

    fun logout() {
        val authRepository = graph.auth
        if (authRepository == null) {
            _state.value = _state.value.copy(error = appwriteUnavailableError().message)
            return
        }
        viewModelScope.launch {
            authRepository.logout().fold(
                onSuccess = { _state.value = DevPocketUiState(configured = graph.config.isReady, isLoading = false) },
                onFailure = { error -> _state.value = _state.value.copy(error = error.toUserMessage()) },
            )
        }
    }

    fun loadNotes() {
        if (_state.value.isLoading) return
        val notesRepository = graph.notes
        if (notesRepository == null) {
            _state.value = _state.value.copy(isLoading = false, error = appwriteUnavailableError().message)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            notesRepository.listNotes().fold(
                onSuccess = { _state.value = _state.value.copy(notes = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.toUserMessage()) },
            )
        }
    }

    fun saveNote(id: String?, input: NoteInput, onSaved: () -> Unit) {
        if (_state.value.isSaving) return
        val validation = validateNoteInput(input)
        if (!validation.isValid) {
            _state.value = _state.value.copy(error = validation.titleError ?: validation.contentError)
            return
        }
        val notesRepository = graph.notes
        if (notesRepository == null) {
            _state.value = _state.value.copy(isSaving = false, error = appwriteUnavailableError().message)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            val result = if (id == null) notesRepository.createNote(input) else notesRepository.updateNote(id, input)
            result.fold(
                onSuccess = { note ->
                    _state.value = _state.value.copy(notes = listOf(note) + _state.value.notes.filterNot { it.id == note.id }, isSaving = false)
                    onSaved()
                },
                onFailure = { _state.value = _state.value.copy(isSaving = false, error = it.toUserMessage()) },
            )
        }
    }

    fun deleteNote(id: String, onDeleted: () -> Unit) {
        val notesRepository = graph.notes
        if (notesRepository == null) {
            _state.value = _state.value.copy(isSaving = false, error = appwriteUnavailableError().message)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            notesRepository.deleteNote(id).fold(
                onSuccess = { _state.value = _state.value.copy(notes = _state.value.notes.filterNot { it.id == id }, isSaving = false); onDeleted() },
                onFailure = { _state.value = _state.value.copy(isSaving = false, error = it.toUserMessage()) },
            )
        }
    }

    fun toggleFavorite(note: Note) {
        saveNote(note.id, NoteInput(note.title, note.content, note.category, note.tags, !note.isFavorite)) {}
    }

    fun setSearch(value: String) { _state.value = _state.value.copy(search = value) }
    fun setCategory(value: String?) { _state.value = _state.value.copy(category = value) }
    fun clearMessage() { _state.value = _state.value.copy(error = null) }

    private fun appwriteUnavailableError() = IllegalStateException("Appwrite is unavailable. Check your project configuration.")

    private fun Throwable.toUserMessage(): String = message?.takeIf(String::isNotBlank)
        ?.substringBefore("\n")?.take(180) ?: "Something went wrong. Check your connection and try again."
}
