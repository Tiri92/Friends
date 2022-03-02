package thierry.friends.ui.usersearchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val mediatorLiveData: MediatorLiveData<UserSearchViewState> =
        MediatorLiveData<UserSearchViewState>()
    private var currentUserData = userRepository.getTheCurrentUserData()
    private var userSearchResult = userRepository.getUserSearchResult()
    private var listOfFriendsRequestsReceived = userRepository.getListOfFriendsRequestsReceived()
    private var listOfFriendsRequestsSent = userRepository.getListOfFriendsRequestsSent()

    init {

        mediatorLiveData.addSource(currentUserData) { currentUser ->
            if (currentUser != null) {
                combine(
                    currentUser,
                    userSearchResult.value,
                    listOfFriendsRequestsReceived.value,
                    listOfFriendsRequestsSent.value
                )
            }
        }
        mediatorLiveData.addSource(userSearchResult) {
            if (it != null) {
                combine(
                    currentUserData.value,
                    it,
                    listOfFriendsRequestsReceived.value,
                    listOfFriendsRequestsSent.value
                )
            }
        }
        mediatorLiveData.addSource(listOfFriendsRequestsReceived) {
            if (it != null) {
                combine(
                    currentUserData.value,
                    userSearchResult.value,
                    it,
                    listOfFriendsRequestsSent.value
                )
            }
        }
        mediatorLiveData.addSource(listOfFriendsRequestsSent) {
            if (!it.isNullOrEmpty()) {
                combine(
                    currentUserData.value,
                    userSearchResult.value,
                    listOfFriendsRequestsReceived.value,
                    it
                )
            }
        }

    }

    private fun combine(
        currentUser: User?,
        userSearchResult: List<User>?,
        listOfFriendsRequestsReceived: List<User>?,
        listOfFriendsRequestsSentById: List<String>?
    ) {
        val listOfFriendsRequestsReceivedById = mutableListOf<String>()
        listOfFriendsRequestsReceivedById.clear()
        listOfFriendsRequestsReceived?.forEach { user ->
            listOfFriendsRequestsReceivedById.add(user.uid)
        }
        val viewState = UserSearchViewState()
        viewState.currentUser = currentUser
        viewState.userSearchResult = userSearchResult
        viewState.listOfFriendsRequestsReceived = listOfFriendsRequestsReceived
        viewState.listOfFriendsRequestsReceivedById = listOfFriendsRequestsReceivedById
        viewState.listOfFriendsRequestsSentById = listOfFriendsRequestsSentById
        mediatorLiveData.value = viewState
    }

    fun getViewState(): LiveData<UserSearchViewState> {
        return mediatorLiveData
    }

    fun searchUser(usernameTyped: String) {
        userRepository.searchUser(usernameTyped)
    }

    fun setCurrentUserData(updatedUser: User) {
        userRepository.setCurrentUserData(updatedUser)
    }

    fun setUserDataWhoSentFriendRequest(
        uidWhoSentFriendRequest: String,
        updatedUser: User
    ) {
        userRepository.setUserDataWhoSentFriendRequest(uidWhoSentFriendRequest, updatedUser)
    }

    fun createTheFriendRequestSent(uidWhoReceived: String, userWhoReceived: User) {
        return userRepository.createTheFriendRequestSent(uidWhoReceived, userWhoReceived)
    }

    fun createTheFriendRequestReceived(
        uidWhoReceived: String,
        uidWhoSent: String,
        userWhoSent: User
    ) {
        return userRepository.createTheFriendRequestReceived(
            uidWhoReceived,
            uidWhoSent,
            userWhoSent
        )
    }

    fun deleteFriendsRequestsWhenItIsProcessed(uid: String, friendId: String) {
        userRepository.deleteFriendsRequestsWhenItIsProcessed(uid, friendId)
    }

}