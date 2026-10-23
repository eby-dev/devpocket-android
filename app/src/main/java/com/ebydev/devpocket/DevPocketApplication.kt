package com.ebydev.devpocket

import android.app.Application
import com.ebydev.devpocket.data.appwrite.AppwriteClient
import com.ebydev.devpocket.data.appwrite.AppwriteConfig
import com.ebydev.devpocket.data.appwrite.AppwriteAuthRepository
import com.ebydev.devpocket.data.appwrite.AppwriteNotesRepository

class DevPocketApplication : Application() {
    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        val config = AppwriteConfig()
        appGraph = if (config.isReady) {
            val client = AppwriteClient(this, config)
            AppGraph(config, AppwriteAuthRepository(client), AppwriteNotesRepository(client, config))
        } else {
            AppGraph(config, null, null)
        }
    }
}

data class AppGraph(
    val config: AppwriteConfig,
    val auth: AppwriteAuthRepository?,
    val notes: AppwriteNotesRepository?,
)
