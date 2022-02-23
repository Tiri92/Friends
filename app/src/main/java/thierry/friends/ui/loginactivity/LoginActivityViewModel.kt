package thierry.friends.ui.loginactivity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    fun isCurrentUserLogged(): Boolean {
        return userRepository.isCurrentUserLogged()
    }

    fun createUserInFirestore() {
        userRepository.createUserInFirestore()
    }

}