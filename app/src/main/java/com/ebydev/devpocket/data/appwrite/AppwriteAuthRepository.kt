package com.ebydev.devpocket.data.appwrite

import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account

data class SignedInUser(val id: String, val email: String, val name: String)

class AppwriteAuthRepository(client: AppwriteClient) {
    private val account = Account(client.client)

    suspend fun currentUser(): Result<SignedInUser?> = runCatching {
        val user = account.get()
        SignedInUser(user.id, user.email, user.name)
    }.recoverCatching { error ->
        if (error is AppwriteException && error.code == 401) null else throw error
    }

    suspend fun login(email: String, password: String): Result<SignedInUser> = runCatching {
        account.createEmailPasswordSession(email.trim(), password)
        val user = account.get()
        SignedInUser(user.id, user.email, user.name)
    }

    suspend fun register(name: String, email: String, password: String): Result<SignedInUser> = runCatching {
        account.create(userId = ID.unique(), email = email.trim(), password = password, name = name.trim())
        account.createEmailPasswordSession(email.trim(), password)
        val user = account.get()
        SignedInUser(user.id, user.email, user.name)
    }

    suspend fun logout(): Result<Unit> = runCatching {
        account.deleteSession("current")
        Unit
    }
}
