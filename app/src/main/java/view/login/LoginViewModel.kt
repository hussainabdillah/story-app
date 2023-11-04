package view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.UserRepository
import data.pref.UserModel
import data.response.LoginResponse
import di.Event
import kotlinx.coroutines.launch

class LoginViewModel(private val uRepository: UserRepository) : ViewModel() {
    val loginResponse: LiveData<LoginResponse> = uRepository.loginResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val toastText: LiveData<Event<String>> = uRepository.toastText

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uRepository.login(email, password)
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            uRepository.saveSession(user)
        }
    }
}