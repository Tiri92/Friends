package thierry.friends.ui.chatfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.LastMessage
import thierry.friends.model.Message
import thierry.friends.model.User
import thierry.friends.repositories.firestore.ChatRepository
import thierry.friends.repositories.firestore.UserRepository
import javax.inject.Inject

@HiltViewModel
class ChatFragmentViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {

    fun callMessagesBetweenTwoUsers(from: String, to: String) {
        chatRepository.callMessagesBetweenTwoUsers(from, to)
    }

    fun getMessagesBetweenTwoUsers(): LiveData<List<Message>> {
        return chatRepository.getMessagesBetweenTwoUsers()
    }

    fun cleanUpTheLiveData() {
        chatRepository.cleanUpTheLiveData()
    }

    fun createNewMessage(newMessage: Message?) {
        chatRepository.createNewMessage(newMessage)
    }

    fun getCurrentUserId(): String {
        return userRepository.getCurrentUserId()
    }

    fun getTheCurrentUserData(): LiveData<User> {
        return userRepository.getTheCurrentUserData()
    }

    fun createLastMessagesSentOrReceived(uid: String, username: String, lastMessage: LastMessage) {
        userRepository.createLastMessagesSentOrReceived(uid, username, lastMessage)
    }

}