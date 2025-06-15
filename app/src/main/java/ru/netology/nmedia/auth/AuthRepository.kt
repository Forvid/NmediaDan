package ru.netology.nmedia.auth

interface AuthRepository {
    suspend fun login(user: String, pass: String): Boolean
    fun logout()
    fun isLoggedIn(): Boolean
}
