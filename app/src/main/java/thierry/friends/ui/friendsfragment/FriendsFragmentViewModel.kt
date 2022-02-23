package thierry.friends.ui.friendsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class FriendsFragmentViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    fun getListOfAllUsers(): LiveData<List<User>> {
        return userRepository.getListOfAllUsers()
    }

}