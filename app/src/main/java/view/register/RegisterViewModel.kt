package view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.UserRepository
import data.response.RegisterResponse
import di.Event
import kotlinx.coroutines.launch

class RegisterViewModel(private val uRepository: UserRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = uRepository.registerResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val toastText: LiveData<Event<String>> = uRepository.toastText
    val registerSuccess: LiveData<RegisterResponse> = uRepository.registerSuccess
    val registerError: LiveData<String> = uRepository.registerError

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            uRepository.register(name, email, password)
        }
    }
}