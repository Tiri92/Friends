package thierry.friends.ui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import thierry.friends.R

class MainActivity : AppCompatActivity() {
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
}