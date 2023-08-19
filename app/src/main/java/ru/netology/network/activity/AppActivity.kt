package ru.netology.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.WallFragment.Companion.userId
import ru.netology.network.auth.AppAuth
import ru.netology.network.viewmodel.AuthViewModel
import javax.inject.Inject

val coordinateCheb = Point(56.1432000, 47.2489000)

val dateFormat = "dd.MM.yyyy"
val timeFormat = "HH:mm"

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_main) {
    @Inject
    lateinit var auth: AppAuth
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel.data.observe(this) {
            invalidateOptionsMenu()
            if (it.id == 0L) {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.loginFragment)
            } else {
                val hi = getString(R.string.hi)
                Toast.makeText(this@AppActivity, "$hi ${it.name}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.wall -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.wallFragment,
                    Bundle().apply
                    {
                        userId = auth.authStateFlow.value.id
                    })
                true
            }

            R.id.posts -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.postFeedFragment)
                true
            }

            R.id.events -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.feedEventFragment)
                true
            }

            R.id.jobs -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.feedJobsFragment,
                    Bundle().apply
                    {
                        userId = auth.authStateFlow.value.id
                    })
                true
            }

            R.id.signout -> {
                auth.removeAuth()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

