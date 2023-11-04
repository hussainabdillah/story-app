package view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.storyapp.databinding.ItemStoryBinding
import data.response.StoryResponse
import view.detail.DetailActivity

class ListStoryAdapter :
    PagingDataAdapter<StoryResponse, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var binding: ItemStoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bindingItem(story)
        }
    }


    class ListViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindingItem(story: StoryResponse) {
            binding.apply{
                storyTitle.text = story.name
                storyDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(storyImage)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra("name", story.name)
                    intent.putExtra("description", story.description)
                    intent.putExtra("photo", story.photoUrl)

                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(
                oldStory: StoryResponse,
                newStory: StoryResponse
            ): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(
                oldStory: StoryResponse,
                newStory: StoryResponse
            ): Boolean {
                return oldStory.name == newStory.name && oldStory.description == newStory.description && oldStory.photoUrl == newStory.photoUrl
            }
        }

    }
}