package thierry.friends.ui.usersearchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
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

    fun listenerOnTheCurrentUserData(): DocumentReference {
        return userRepository.listenerOnTheCurrentUserData()
    }

    fun setCurrentUserData(updatedUser: User) {
        userRepository.setCurrentUserData(updatedUser)
    }

    fun setUserDataWhoReceivedFriendRequest(
        uidWhoReceivedFriendRequest: String,
        updatedUser: User
    ) {
        userRepository.setUserDataWhoReceivedFriendRequest(uidWhoReceivedFriendRequest, updatedUser)
    }

    fun createFriendsRequestsSentCollection(uidWhoReceived: String, userWhoReceived: User) {
        return userRepository.createFriendsRequestsSentCollection(uidWhoReceived, userWhoReceived)
    }

    fun createFriendsRequestsReceivedCollection(
        uidWhoReceived: String,
        uidWhoSent: String,
        userWhoSent: User
    ) {
        return userRepository.createFriendsRequestsReceivedCollection(
            uidWhoReceived,
            uidWhoSent,
            userWhoSent
        )
    }

    fun getListOfFriendsRequests(): LiveData<List<User>> {
        return userRepository.getListOfFriendsRequests()
    }

    fun cleanUpTheLiveData() {
        userRepository.cleanUpTheLiveData()
    }

    fun addingUserToFriendsCollection(uid: String, friendId: String, friend: User) {
        userRepository.addingUserToFriendsCollection(uid, friendId, friend)
    }

}