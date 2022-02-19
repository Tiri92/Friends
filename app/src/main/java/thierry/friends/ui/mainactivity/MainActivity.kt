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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import dagger.hilt.android.AndroidEntryPoint
import thierry.friends.R
import thierry.friends.databinding.ActivityMainBinding
import thierry.friends.ui.loginactivity.LoginActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        return super.onOptionsItemSelected(item)
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