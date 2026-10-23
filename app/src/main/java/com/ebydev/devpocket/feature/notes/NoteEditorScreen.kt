package com.ebydev.devpocket.feature.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebydev.devpocket.domain.model.Note
import com.ebydev.devpocket.domain.model.NoteCategory
import com.ebydev.devpocket.domain.model.NoteInput
import com.ebydev.devpocket.domain.model.normalizeTags

@Composable
fun NoteEditorScreen(
    note: Note?,
    isSaving: Boolean,
    error: String?,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    onSave: (NoteInput) -> Unit,
) {
    var title by rememberSaveable(note?.id) { mutableStateOf(note?.title.orEmpty()) }
    var content by rememberSaveable(note?.id) { mutableStateOf(note?.content.orEmpty()) }
    var tags by rememberSaveable(note?.id) { mutableStateOf(note?.tags?.joinToString(", ").orEmpty()) }
    var category by rememberSaveable(note?.id) { mutableStateOf(note?.category ?: NoteCategory.KOTLIN) }
    var favorite by rememberSaveable(note?.id) { mutableStateOf(note?.isFavorite ?: false) }
    var categoryMenu by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    Scaffold { insets ->
        Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back") }
                Text(if (note == null) "New note" else "Edit note", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(140); titleError = null; onClearError() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Retry requests with exponential backoff") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = { Text(titleError ?: "${title.length}/120") },
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it; contentError = null; onClearError() },
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    label = { Text("Note or code snippet") },
                    placeholder = { Text("Write down the useful part…") },
                    isError = contentError != null,
                )
                if (contentError != null) Text(contentError.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Text("Category", style = MaterialTheme.typography.labelLarge)
                androidx.compose.material3.OutlinedButton(onClick = { categoryMenu = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(category.label, modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.ExpandMore, contentDescription = "Choose category")
                }
                DropdownMenu(expanded = categoryMenu, onDismissRequest = { categoryMenu = false }) {
                    NoteCategory.entries.forEach { item -> DropdownMenuItem(text = { Text(item.label) }, onClick = { category = item; categoryMenu = false }) }
                }
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tags") },
                    placeholder = { Text("coroutines, compose, networking") },
                    supportingText = { Text("Separate tags with commas") },
                    singleLine = true,
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("Bookmark", style = MaterialTheme.typography.titleSmall)
                        Text("Keep this note easy to spot.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = favorite, onCheckedChange = { favorite = it })
                }
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
            }
            Button(
                onClick = {
                    titleError = when {
                        title.trim().isEmpty() -> "Add a title to continue."
                        title.trim().length > 120 -> "Keep the title under 120 characters."
                        else -> null
                    }
                    contentError = if (content.trim().isEmpty()) "Add some content to continue." else null
                    if (titleError == null && contentError == null) {
                        onSave(NoteInput(title.trim(), content.trim(), category, normalizeTags(tags), favorite))
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(52.dp),
            ) {
                if (isSaving) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(22.dp)) else Text("Save note")
            }
        }
    }
}
