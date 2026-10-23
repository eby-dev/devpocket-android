package com.ebydev.devpocket.domain.model

enum class NoteCategory(val label: String) {
    KOTLIN("Kotlin"), ANDROID("Android"), ARCHITECTURE("Architecture"),
    DEBUGGING("Debugging"), TOOLS("Tools"), OTHER("Other");

    companion object {
        fun from(value: String): NoteCategory = entries.firstOrNull { it.label == value } ?: OTHER
    }
}

data class Note(
    val id: String,
    val ownerId: String,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val tags: List<String>,
    val isFavorite: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

data class NoteInput(
    val title: String,
    val content: String,
    val category: NoteCategory,
    val tags: List<String>,
    val isFavorite: Boolean,
)

data class NoteValidation(val titleError: String? = null, val contentError: String? = null) {
    val isValid: Boolean get() = titleError == null && contentError == null
}

fun validateNoteInput(input: NoteInput): NoteValidation = NoteValidation(
    titleError = when {
        input.title.trim().isEmpty() -> "Add a title to continue."
        input.title.trim().length > 120 -> "Keep the title under 120 characters."
        else -> null
    },
    contentError = if (input.content.trim().isEmpty()) "Add some content to continue." else null,
)

fun normalizeTags(raw: String): List<String> = raw.split(',')
    .map(String::trim)
    .filter(String::isNotEmpty)
    .distinctBy(String::lowercase)
    .take(12)
