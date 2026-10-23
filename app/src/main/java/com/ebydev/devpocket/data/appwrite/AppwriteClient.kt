package com.ebydev.devpocket.data.appwrite

import android.content.Context
import io.appwrite.Client

class AppwriteClient(context: Context, config: AppwriteConfig) {
    val client: Client = Client(context.applicationContext)
        .setEndpoint(config.endpoint)
        .setProject(config.projectId)
}
