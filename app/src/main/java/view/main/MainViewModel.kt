package view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import data.repository.UserRepository
import data.pref.UserModel
import data.response.StoryListResponse
import kotlinx.coroutines.launch

class MainViewModel(private val uRepository: UserRepository) : ViewModel() {

    val storiesListResponse: LiveData<StoryListResponse> = uRepository.storyListResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading

    fun getUser(): LiveData<UserModel> {
        return uRepository.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            uRepository.logout()
        }
    }

    fun getStories(token: String) {
        viewModelScope.launch {
            uRepository.getStories(token)
        }
    }
}