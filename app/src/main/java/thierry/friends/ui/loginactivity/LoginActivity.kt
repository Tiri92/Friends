package thierry.friends.ui.loginactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import thierry.friends.R
import thierry.friends.ui.mainactivity.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                handleResponseAfterSignIn(result?.resultCode!!, result.data!!)
            }

        startSignInActivity()
    }

    private fun startSignInActivity() {
        val providers = listOf(
            EmailBuilder().build()
        )

        activityResultLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .build()
        )
    }

    private fun handleResponseAfterSignIn(resultCode: Int, data: Intent) {
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == RESULT_OK) {
            //loginActivityViewModel.createUser()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (response == null) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.authentication_canceled),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (response.error != null) {
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.error_no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        finish()
    }

}