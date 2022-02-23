package thierry.friends.ui.chatfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.Message
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

    fun listenerOnTheCurrentUserData(): DocumentReference {
        return userRepository.listenerOnTheCurrentUserData()
    }

}