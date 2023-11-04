package view.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import di.ViewModelFactory
import view.main.MainActivity
import view.main.MainViewModel
import view.welcome.WelcomeActivity

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_SCREEN_TIMEOUT = 3000
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setupUser()
    }

    fun setupUser() {
        mainViewModel.getUser().observe(this@SplashScreenActivity) {
            token = it.token
            if (!it.isLogin) {
                moveActivity()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun moveActivity() {
        Handler().postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}