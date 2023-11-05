package ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import data.repository.UserRepository
import data.pref.UserModel
import data.response.UploadStoryResponse
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(private val uRepository: UserRepository) : ViewModel() {
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val uploadResponse: LiveData<UploadStoryResponse> = uRepository.uploadStoryResponse

    fun uploadStory(token: String, imageFile: File, description: String) {
        viewModelScope.launch {
            uRepository.uploadStory(token, imageFile, description)
        }
    }

    fun getUser(): LiveData<UserModel>  {
        return uRepository.getUser().asLiveData()
    }
}