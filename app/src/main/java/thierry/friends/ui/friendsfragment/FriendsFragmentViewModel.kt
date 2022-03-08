package thierry.friends.ui.friendsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.LastChatFragmentOpening
import thierry.friends.model.LastMessage
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
    private var listOfLastChatFragmentOpening = userRepository.getListOfLastChatFragmentOpening()
    private var listOfLastMessages = userRepository.getListOfLastMessages()

    init {

        mediatorLiveData.addSource(currentUser) { currentUser ->
            if (currentUser != null) {
                combine(
                    currentUser,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith.value,
                    listOfLastChatFragmentOpening.value,
                    listOfLastMessages.value
                )
            }
        }

        mediatorLiveData.addSource(listOfAllUsers) { listOfAllUsers ->
            if (!listOfAllUsers.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers,
                    listSortedByLastFriendsCurrentUserSpokeWith.value,
                    listOfLastChatFragmentOpening.value,
                    listOfLastMessages.value
                )
            }
        }
        mediatorLiveData.addSource(listSortedByLastFriendsCurrentUserSpokeWith) { listSortedByLastFriendsCurrentUserSpokeWith ->
            if (!listSortedByLastFriendsCurrentUserSpokeWith.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith,
                    listOfLastChatFragmentOpening.value,
                    listOfLastMessages.value
                )
            }
        }

        mediatorLiveData.addSource(listOfLastChatFragmentOpening) { listOfLastChatFragmentOpening ->
            if (!listOfLastChatFragmentOpening.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith.value,
                    listOfLastChatFragmentOpening,
                    listOfLastMessages.value
                )
            }
        }

        mediatorLiveData.addSource(listOfLastMessages) { listOfLastMessages ->
            if (!listOfLastMessages.isNullOrEmpty()) {
                combine(
                    currentUser.value,
                    listOfAllUsers.value,
                    listSortedByLastFriendsCurrentUserSpokeWith.value,
                    listOfLastChatFragmentOpening.value,
                    listOfLastMessages
                )
            }
        }

    }

    private fun combine(
        currentUser: User?,
        listOfAllUsers: List<User>?,
        listSortedByLastFriendsCurrentUserSpokeWith: List<String>?,
        listOfLastChatFragmentOpening: List<LastChatFragmentOpening>?,
        listOfLastMessages: List<LastMessage>?
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
                    viewState.currentUser = currentUser
                    viewState.listOfFriends = listOfFriendsSorted
                    mediatorLiveData.value = viewState
                } else {
                    viewState.currentUser = currentUser
                    viewState.listOfFriends = listOfFriendsSorted
                    mediatorLiveData.value = viewState
                }
            } else {
                viewState.currentUser = currentUser
                viewState.listOfFriends = listOfFriends
                mediatorLiveData.value = viewState
            }
        }
        listOfLastMessages?.let {
            val listOfUnreadMessages = mutableListOf<LastMessage>()
            listOfUnreadMessages.clear()
            it.forEach { lastMessage ->
                listOfLastChatFragmentOpening?.let { it2 ->
                    it2.forEach { lastChatFragmentOpening ->
                        if (lastMessage.uid == lastChatFragmentOpening.uid) {
                            if (lastMessage.date > lastChatFragmentOpening.date) {
                                listOfUnreadMessages.add(lastMessage)
                            }
                        }
                    }
                }
            }
            viewState.listOfUnreadMessages = listOfUnreadMessages
            mediatorLiveData.value = viewState
        }
    }

    fun getViewState(): LiveData<FriendsViewState> {
        return mediatorLiveData
    }

}