package view.main

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import di.ViewModelFactory
import view.upload.UploadActivity
import view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: ListStoryAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupAdapter()
        setupUser()
        setupAction()
    }


    override fun onResume() {
        super.onResume()
        setupData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                mainViewModel.getUser().observe(this@MainActivity) {
                    Log.d(TAG, "Token: ${it.token}")
                    Log.d(TAG, "Name: ${it.email}")
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupAdapter() {
        storyAdapter = ListStoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter( footer = LoadingStateAdapter { storyAdapter.retry() })
        }

    }

    private fun setupUser() {
        mainViewModel.getUser().observe(this@MainActivity) {
            token = it.token
            Log.d(TAG, "setupUser: $token")
            if (!it.isLogin) {
                moveActivity()
            } else {
                setupData()
            }
        }
    }

    private fun moveActivity() {
        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
        finish()
    }

    private fun setupData() {
        showLoading()
        mainViewModel.getUser().observe(this@MainActivity) {
            token = "Bearer " + it.token
            Log.d(TAG, "setupUser: $token")
            Log.d(TAG, "setupName: ${it.email}")
//            mainViewModel.storyPaging(token)

        }
//        mainViewModel.storiesListPaging.observe(this@MainActivity) {
//            storyAdapter.submitData(lifecycle, it)
//        }
//        storyAdapter.withLoadStateFooter(
//            footer = LoadingStateAdapter { storyAdapter.retry() }
//        )
        mainViewModel.storiesListPaging.observe(this, {
            if (it != null) {
                storyAdapter.submitData(lifecycle, it)
            }
            else {
                Log.d(TAG, "setupData: null")
            }
        })

//        mainViewModel.storiesListResponse.observe(this, { storyListResponse ->
//            if (storyListResponse != null) {
//                storyAdapter.submitList(storyListResponse.listStory)
//            }
//        })
    }

    private fun setupAction() {
        binding.uploadStoryFab.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun showLoading() {
        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

}