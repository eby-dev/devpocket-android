package com.ebydev.devpocket.feature.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ebydev.devpocket.app.DevPocketUiState
import com.ebydev.devpocket.domain.model.Note
import com.ebydev.devpocket.domain.model.NoteCategory

@Composable
fun LibraryScreen(
    state: DevPocketUiState,
    onSearch: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onRetry: () -> Unit,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onFavorite: (Note) -> Unit,
    onLogout: () -> Unit,
) {
    val filtered = state.notes.filter { note ->
        (state.category == null || note.category.label == state.category) &&
            (state.search.isBlank() || listOf(note.title, note.content, note.tags.joinToString(" "))
                .any { it.contains(state.search.trim(), ignoreCase = true) })
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Rounded.Add, contentDescription = "Add note")
            }
        },
    ) { insets ->
        Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("DEVELOPER LIBRARY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Your DevPocket", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Sign out") }
            }
            Text("${state.notes.size} ${if (state.notes.size == 1) "note" else "notes"} saved", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = state.search,
                onValueChange = onSearch,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search your notes") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            )
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.category == null, onClick = { onCategory(null) }, label = { Text("All") })
                NoteCategory.entries.forEach { category ->
                    FilterChip(selected = state.category == category.label, onClick = { onCategory(category.label) }, label = { Text(category.label) })
                }
            }
            Spacer(Modifier.height(12.dp))
            if (state.error != null && state.notes.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.error, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onRetry) { Text("Retry") }
                }
            }
            when {
                state.isLoading -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Loading your library…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.error != null && state.notes.isEmpty() -> EmptyMessage("Couldn’t load your notes", state.error, "Try again", onRetry)
                state.notes.isEmpty() -> EmptyMessage("Your pocket is empty", "Save a fix, code snippet, or idea so it’s easy to find later.", "Create your first note", onAdd)
                filtered.isEmpty() -> EmptyMessage("No matching notes", "Try another keyword or category.", "Clear filters", { onSearch(""); onCategory(null) })
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)) {
                    items(filtered, key = Note::id) { note ->
                        ElevatedCard(onClick = { onOpen(note.id) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.category.label.uppercase(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { onFavorite(note) }, modifier = Modifier.size(36.dp)) {
                                        Icon(if (note.isFavorite) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder, contentDescription = if (note.isFavorite) "Remove bookmark" else "Bookmark note", tint = if (note.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(note.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(5.dp))
                                Text(note.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                if (note.tags.isNotEmpty()) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(note.tags.joinToString("  ·  ") { "#${it}" }, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage(title: String, body: String, action: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(bottom = 36.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onAction) { Text(action) }
    }
}
