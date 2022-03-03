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
    private var listSortedByLastFriendsCurrentUserSpokeWith =
        userRepository.getListSortedByLastFriendsCurrentUserSpokeWith()

    init {

        mediatorLiveData.addSource(currentUser) { currentUser ->
            if (currentUser != null) {
                combine(
                    currentUser,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith.value
                )
            }
        }

        mediatorLiveData.addSource(listOfAllUsers) { listOfAllUsers ->
            if (!listOfAllUsers.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers,
                    listSortedByLastFriendsCurrentUserSpokeWith.value
                )
            }
        }
        mediatorLiveData.addSource(listSortedByLastFriendsCurrentUserSpokeWith) { listSortedByLastFriendsCurrentUserSpokeWith ->
            if (!listSortedByLastFriendsCurrentUserSpokeWith.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith
                )
            }
        }

    }

    private fun combine(
        currentUser: User?,
        listOfAllUsers: List<User>?,
        listSortedByLastFriendsCurrentUserSpokeWith: List<String>?
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
            if (!listSortedByLastFriendsCurrentUserSpokeWith.isNullOrEmpty()) {
                val listOfFriendsSorted = mutableListOf<User>()
                listOfFriendsSorted.clear()
                listSortedByLastFriendsCurrentUserSpokeWith.forEach { id ->
                    val friendFound = listOfFriends.find { predicate -> predicate.uid == id }
                    if (friendFound != null) {
                        listOfFriendsSorted.add(friendFound)
                    }
                }
                if (listOfFriendsSorted.count() != listOfFriends.count()) {
                    listOfFriends.forEach { friend ->
                        if (!listOfFriendsSorted.contains(friend)) {
                            listOfFriendsSorted.add(friend)
                        }
                    }
                    viewState.listOfFriends = listOfFriendsSorted
                    mediatorLiveData.value = viewState
                } else {
                    viewState.listOfFriends = listOfFriendsSorted
                    mediatorLiveData.value = viewState
                }
            } else {
                viewState.listOfFriends = listOfFriends
                mediatorLiveData.value = viewState
            }
        }
    }

    fun getViewState(): LiveData<FriendsViewState> {
        return mediatorLiveData
    }

}