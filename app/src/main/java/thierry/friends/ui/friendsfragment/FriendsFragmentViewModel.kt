package thierry.friends.ui.friendsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class FriendsFragmentViewModel @Inject constructor(userRepository: UserRepository) :
    ViewModel() {

    private val mediatorLiveData: MediatorLiveData<FriendsViewState> =
        MediatorLiveData<FriendsViewState>()
    private var currentUser = userRepository.getTheCurrentUserData()
    private var listOfAllUsers = userRepository.getListOfAllUsers()

    init {

        mediatorLiveData.addSource(currentUser) { currentUser ->
            if (currentUser != null) {
                combine(currentUser, listOfAllUsers.value)
            }
        }

        mediatorLiveData.addSource(listOfAllUsers) { listOfAllUsers ->
            if (!listOfAllUsers.isNullOrEmpty()) {
                combine(currentUser.value, listOfAllUsers)
            }
        }

    }

    private fun combine(
        currentUser: User?,
        listOfAllUsers: List<User>?
    ) {
        val viewState = FriendsViewState()
        if (currentUser != null && !listOfAllUsers.isNullOrEmpty()) {
            val listOfFriends = mutableListOf<User>()
            listOfFriends.clear()
            for (friend in currentUser.listOfFriends) {
                val friendFound = listOfAllUsers.find { predicate -> predicate.uid == friend }
                if (friendFound != null) {
                    listOfFriends.add(friendFound)
                }
            }
            viewState.listOfFriends = listOfFriends
            mediatorLiveData.value = viewState
        }
    }

    fun getViewState(): LiveData<FriendsViewState> {
        return mediatorLiveData
    }

}