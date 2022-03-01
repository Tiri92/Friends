package thierry.friends.ui.mainactivity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    fun isCurrentUserLogged(): Boolean {
        return userRepository.isCurrentUserLogged()
    }

    fun logout(context: Context): Task<Void?> {
        return userRepository.logout(context)
    }

    fun getTheCurrentUserData(): LiveData<User> {
        return userRepository.getTheCurrentUserData()
    }

    fun setUserFcmToken(currentUser: User) {
        userRepository.setCurrentUserData(currentUser)
    }

}