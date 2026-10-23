package com.ebydev.devpocket.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ebydev.devpocket.app.DevPocketViewModel
import com.ebydev.devpocket.feature.auth.AuthScreen
import com.ebydev.devpocket.feature.home.LibraryScreen
import com.ebydev.devpocket.feature.notes.NoteDetailScreen
import com.ebydev.devpocket.feature.notes.NoteEditorScreen

@Composable
fun AppNavHost(viewModel: DevPocketViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    if (state.isRestoringSession) {
        SessionRestoringScreen()
        return
    }

    LaunchedEffect(state.user?.id) {
        if (state.user != null) {
            if (navController.currentBackStackEntry?.destination?.route != "library") {
                navController.navigate("library") {
                    popUpTo("auth") { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else if (navController.currentBackStackEntry?.destination?.route == "library") {
            navController.navigate("auth") { popUpTo("library") { inclusive = true }; launchSingleTop = true }
        }
    }

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            if (state.user != null) {
                SessionRestoringScreen()
            } else {
                AuthScreen(state = state, onLogin = viewModel::login, onRegister = viewModel::register)
            }
        }
        composable("library") {
            LibraryScreen(
                state = state,
                onSearch = viewModel::setSearch,
                onCategory = viewModel::setCategory,
                onRetry = viewModel::loadNotes,
                onAdd = { navController.navigate("editor") },
                onOpen = { id -> navController.navigate("detail/${Uri.encode(id)}") },
                onFavorite = viewModel::toggleFavorite,
                onLogout = viewModel::logout,
            )
        }
        composable("editor") {
            NoteEditorScreen(
                note = null,
                isSaving = state.isSaving,
                error = state.error,
                onClearError = viewModel::clearMessage,
                onBack = { navController.popBackStack() },
                onSave = { input -> viewModel.saveNote(null, input) { navController.popBackStack() } },
            )
        }
        composable("detail/{noteId}", arguments = listOf(navArgument("noteId") { type = NavType.StringType })) { entry ->
            val id = Uri.decode(entry.arguments?.getString("noteId").orEmpty())
            val note = state.notes.firstOrNull { it.id == id }
            NoteDetailScreen(
                note = note,
                isLoading = state.isLoading,
                error = state.error,
                onBack = { navController.popBackStack() },
                onRetry = viewModel::loadNotes,
                onEdit = { navController.navigate("edit/${Uri.encode(id)}") },
                onFavorite = { note?.let(viewModel::toggleFavorite) },
                onDelete = { viewModel.deleteNote(id) { navController.popBackStack() } },
            )
        }
        composable("edit/{noteId}", arguments = listOf(navArgument("noteId") { type = NavType.StringType })) { entry ->
            val id = Uri.decode(entry.arguments?.getString("noteId").orEmpty())
            NoteEditorScreen(
                note = state.notes.firstOrNull { it.id == id },
                isSaving = state.isSaving,
                error = state.error,
                onClearError = viewModel::clearMessage,
                onBack = { navController.popBackStack() },
                onSave = { input -> viewModel.saveNote(id, input) { navController.popBackStack() } },
            )
        }
    }
}

@Composable
private fun SessionRestoringScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("DevPocket", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Restoring your session…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
