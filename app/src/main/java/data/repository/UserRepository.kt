package data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import data.api.ApiService
import data.pref.UserModel
import data.pref.UserPreference
import data.response.LoginResponse
import data.response.RegisterResponse
import data.response.StoryListResponse
import data.response.UploadStoryResponse
import di.Event
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.UnknownHostException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _storyListResponse = MutableLiveData<StoryListResponse>()
    val storyListResponse: LiveData<StoryListResponse> = _storyListResponse

    private val _uploadStoryResponse = MutableLiveData<UploadStoryResponse>()
    val uploadStoryResponse: LiveData<UploadStoryResponse> = _uploadStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _registerSuccess = MutableLiveData<RegisterResponse>()
    val registerSuccess: LiveData<RegisterResponse>
        get() = _registerSuccess

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String>
        get() = _registerError

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                try {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        _registerResponse.value = response.body()
                        _toastText.value = Event(response.body()?.message.toString())
                    } else {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        val error = jsonObject?.getBoolean("error")
                        val message = jsonObject?.getString("message")
                        _registerResponse.value = RegisterResponse(error, message)
                        _toastText.value = Event(
                            "${response.message()} ${response.code()}, $message"
                        )
                        Log.e(
                            "Register",
                            "onResponse: ${response.message()}, ${response.code()} $message"
                        )
                    }
                } catch (e: JSONException) {
                    _toastText.value = Event(e.message.toString())
                    Log.e("JSONException", "onResponse: ${e.message.toString()}")
                } catch (e: Exception) {
                    _toastText.value = Event(e.message.toString())
                    Log.e("Exception", "onResponse: ${e.message.toString()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                when (t) {
                    is UnknownHostException -> {
                        _toastText.value = Event("No Internet Connection")
                        Log.e("UnknownHostException", "onFailure: ${t.message.toString()}")
                    }

                    else -> {
                        _toastText.value = Event(t.message.toString())
                        Log.e("postRegister", "onFailure: ${t.message.toString()}")
                    }
                }
            }
        })
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.login(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                    val error = jsonObject?.getBoolean("error")
                    val message = jsonObject?.getString("message")
                    _loginResponse.value = LoginResponse(null, error, message)
                    _toastText.value = Event(
                        "${response.message()} ${response.code()}, $message"
                    )
                    Log.e(
                        "Login",
                        "onResponse: ${response.message()}, ${response.code()} $message"
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getStories(token: String) {
        _isLoading.value = true
        val client = apiService.getStories(token)

        client.enqueue(object : Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _storyListResponse.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStory(token: String, imageFile: File, description: String) {
        _isLoading.value = true
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = apiService.uploadStory(token, multipartBody, requestBody)

        client.enqueue(object : Callback<UploadStoryResponse> {
            override fun onResponse(
                call: Call<UploadStoryResponse>,
                response: Response<UploadStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _uploadStoryResponse.value = response.body()
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}