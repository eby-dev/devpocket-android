package com.ebydev.devpocket.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteValidationTest {
    @Test
    fun rejectsBlankTitleAndBody() {
        val result = validateNoteInput(NoteInput("  ", "\n", NoteCategory.KOTLIN, emptyList(), false))
        assertFalse(result.isValid)
        assertEquals("Add a title to continue.", result.titleError)
        assertEquals("Add some content to continue.", result.contentError)
    }

    @Test
    fun enforcesTitleLimit() {
        val result = validateNoteInput(NoteInput("x".repeat(121), "body", NoteCategory.OTHER, emptyList(), false))
        assertEquals("Keep the title under 120 characters.", result.titleError)
    }

    @Test
    fun acceptsValidNoteAndNormalizesTags() {
        val result = validateNoteInput(NoteInput("A useful title", "body", NoteCategory.ANDROID, emptyList(), false))
        assertTrue(result.isValid)
        assertEquals(listOf("compose", "android"), normalizeTags("compose, android, Compose, ,"))
    }
}
