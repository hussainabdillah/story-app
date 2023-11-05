package ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import data.repository.UserRepository
import data.pref.UserModel
import data.response.StoryListResponse
import data.response.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(private val uRepository: UserRepository) : ViewModel() {

    val storiesListResponse: LiveData<StoryListResponse> = uRepository.storyListResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val storiesListPaging: LiveData<PagingData<StoryResponse>> =
        uRepository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return uRepository.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            uRepository.logout()
        }
    }

}