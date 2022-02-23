package thierry.friends.repositories.firestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import thierry.friends.model.Message
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_MESSAGES = "messages"

@Singleton
class ChatRepository @Inject constructor() {

    /** Get the messages collection **/
    private fun getMessagesCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_MESSAGES)
    }

    /** Get messages between two users with snapshot listener for display the messages of chat in real time **/

    private val listOfAllMessages = MutableLiveData<List<Message>>()

    fun callMessagesBetweenTwoUsers(from: String, to: String) {
        val mutableListOfMessages: MutableList<Message> = mutableListOf()
        getMessagesCollection().whereIn(
            "between",
            listOf(listOf(from, to), listOf(to, from))
        ).orderBy("date", Query.Direction.ASCENDING).addSnapshotListener { value, _ ->
            if (value != null) {
                mutableListOfMessages.clear()
                for (document in value.documents) {
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        mutableListOfMessages.add(message)
                    }
                }
            }
            listOfAllMessages.value = mutableListOfMessages
        }
    }

    fun getMessagesBetweenTwoUsers(): LiveData<List<Message>> {
        return listOfAllMessages
    }

    /** **/

    /** Assign an empty list for listOfAllMessages when the user exits the chat to avoid displaying the wrong messages
     ** if the user opens a new chat because liveData keeps in memory the last value assigned to them
     **/
    fun cleanUpTheLiveData() {
        listOfAllMessages.value = mutableListOf()
    }

    /** Create a new message in the firestore database **/
    fun createNewMessage(newMessage: Message?): Task<Void?>? {
        return newMessage?.let { getMessagesCollection().document().set(it) }
    }

}