package data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import data.api.ApiService
import data.pref.UserPreference
import data.response.StoryResponse
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.net.UnknownHostException

class StoryPagingSource(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) : PagingSource<Int, StoryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val token = userPreference.getUser().first().token
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer $token", position, params.loadSize)

            if (token.isNotEmpty()) {
//                val response = apiService.getListStories(token, position, params.loadSize)
                if (responseData.isSuccessful) {
                    Log.d("Story Paging Source", "Load Result: ${responseData.body()}")
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    val jsonObject = responseData.errorBody()?.string()?.let { JSONObject(it) }
                    val message = jsonObject?.getString("message")
                    Log.e(
                        "getListStories",
                        "Load Error: ${responseData.message()}, ${responseData.code()} $message"
                    )
                    LoadResult.Error(Exception("Something went wrong"))
                }
            } else {
                Log.e("Token", "Load Error: $token")
                LoadResult.Error(Exception("Token is Empty"))
            }
        } catch (e: UnknownHostException) {
            Log.e("UnknownHostException", "Load Error: ${e.message}")
            return LoadResult.Error(Exception("No Internet Connection"))
        } catch (e: Exception) {
            Log.e("Exception", "Load Error: ${e.message}")
            return LoadResult.Error(Exception(e.message))
        }
//            LoadResult.Page(
//                data = responseData.body()?.listStory ?: emptyList(),
//                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
//                nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
//            )
//        } catch (exception: Exception) {
//            Log.e("Exception", "Load Error: ${exception.message}")
//            return LoadResult.Error(exception)
//        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}