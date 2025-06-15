package ru.netology.nmedia.auth

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    fun login(user: String, pass: String) {
        viewModelScope.launch {
            val success = repository.login(user, pass)
            _loginResult.value = success
        }
    }

    fun logout() {
        repository.logout()
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}
