package data.response

import com.google.gson.annotations.SerializedName

data class StoryListResponse(

    @field:SerializedName("listStory")
    val listStory: List<StoryResponse> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
