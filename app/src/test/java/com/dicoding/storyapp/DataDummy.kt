package com.dicoding.storyapp

import data.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponse(
                "https://story-api.dicoding.dev/images/stories/photos-1667058715359_iRitxc-I.jpg",
                i.toString(),
                "hussain",
                "Testing",
                -6.1696896,
                i.toString(),
                106.633315,
            )
            items.add(story)
        }
        return items
    }
}