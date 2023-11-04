package di

import android.content.Context
import data.repository.UserRepository
import data.api.ApiConfig
import data.pref.UserPreference
import data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(userPreference, apiService)
    }
}