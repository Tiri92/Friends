package thierry.friends.ui.mainactivity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import dagger.hilt.android.AndroidEntryPoint
import thierry.friends.R
import thierry.friends.databinding.ActivityMainBinding
import thierry.friends.model.User
import thierry.friends.ui.friendsfragment.FriendsFragment
import thierry.friends.ui.loginactivity.LoginActivity
import thierry.friends.ui.secondfragment.SecondFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var authStateListener: AuthStateListener
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Are you sure you want to log out ?")
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            viewModel.logout(this)
                        }
                        .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                        }
                        .show()
                }
            }
            true
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.friends -> {
                    openFragment(FriendsFragment.newInstance())
                    true
                }
                R.id.second -> {
                    openFragment(SecondFragment.newInstance())
                    true
                }
                R.id.google_map -> {
                    Toast.makeText(baseContext, "yeah3!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        viewModel.listenerOnTheCurrentUserData().addSnapshotListener { value, _ ->
            if (value != null) {
                val currentUserInFirestore = value.toObject(User::class.java)
                if (currentUserInFirestore != null) {
                    val userEmailTextView: TextView =
                        binding.navView.getHeaderView(0).findViewById(R.id.user_email)
                    userEmailTextView.text = currentUserInFirestore.userEmail
                    val usernameTextView: TextView =
                        binding.navView.getHeaderView(0).findViewById(R.id.username)
                    usernameTextView.text = currentUserInFirestore.username
                    val userPic: ImageView =
                        binding.navView.getHeaderView(0).findViewById(R.id.imageview)
                    Glide.with(baseContext).load(currentUserInFirestore.userPicture).circleCrop()
                        .into(userPic)
                }
            }
        }

        authStateListener = AuthStateListener {
            if (!viewModel.isCurrentUserLogged()) {
                comeBackToLoginActivity()
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        connectionListener()
    }

    private fun comeBackToLoginActivity() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        val intent = Intent(this, LoginActivity::class.java)
        ActivityCompat.startActivity(this, intent, null)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Toast.makeText(baseContext, "yeah!", Toast.LENGTH_SHORT).show()
            }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFragment(fragmentInstance: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragmentInstance)
            .commit()
    }

    private fun connectionListener() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)

        val networkRequest = builder.build()
        connectivityManager.registerNetworkCallback(networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    runOnUiThread {
//                        viewModel.getCurrentConnectionState(true)
//                        viewModel.setCurrentConnectionState()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    runOnUiThread {
//                        viewModel.getCurrentConnectionState(false)
//                        viewModel.setCurrentConnectionState()
                    }
                }
            })
    }

}