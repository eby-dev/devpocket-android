package com.ebydev.devpocket.data.appwrite

import com.ebydev.devpocket.BuildConfig

data class AppwriteConfig(
    val endpoint: String = BuildConfig.APPWRITE_ENDPOINT,
    val projectId: String = BuildConfig.APPWRITE_PROJECT_ID,
    val databaseId: String = BuildConfig.APPWRITE_DATABASE_ID,
    val tableId: String = BuildConfig.APPWRITE_TABLE_ID,
) {
    val isReady: Boolean
        get() = endpoint.startsWith("https://") && projectId.isNotBlank() &&
            databaseId.isNotBlank() && tableId.isNotBlank()
}
