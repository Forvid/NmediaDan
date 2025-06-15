package ru.netology.nmedia.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private var loggedIn = false

    override suspend fun login(user: String, pass: String): Boolean {
        loggedIn = user.isNotBlank() && pass.isNotBlank()
        return loggedIn
    }

    override fun logout() {
        loggedIn = false
    }

    override fun isLoggedIn(): Boolean = loggedIn
}
