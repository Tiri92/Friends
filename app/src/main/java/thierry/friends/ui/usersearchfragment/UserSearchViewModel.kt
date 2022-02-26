package thierry.friends.ui.usersearchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    fun searchUser(usernameTyped: String) {
        userRepository.searchUser(usernameTyped)
    }

    fun getTheUserSearchResult(): LiveData<List<User>> {
        return userRepository.getTheUserSearchResult()
    }

}