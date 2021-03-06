package thierry.friends.repositories.firestore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import thierry.friends.model.LastChatFragmentOpening
import thierry.friends.model.LastMessage
import thierry.friends.model.User
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_USERS = "users"
private const val COLLECTION_FRIENDS_REQUESTS_SENT = "friendsRequestsSent"
private const val COLLECTION_FRIENDS_REQUESTS_RECEIVED = "friendsRequestsReceived"
private const val COLLECTION_LAST_MESSAGES = "lastMessages"
private const val COLLECTION_LAST_CHAT_FRAGMENT_OPENING = "lastChatFragmentOpening"

@Singleton
class UserRepository @Inject constructor() {

    /** get the users collection **/
    private fun getUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
    }

    /** Get the current firebase user **/
    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    /** Get the id of the current user **/
    fun getCurrentUserId(): String {
        return getCurrentUser()?.uid.toString()
    }

    /** Is the user logged in **/
    fun isCurrentUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    /** Logout the user **/
    fun logout(context: Context?): Task<Void?> {
        return AuthUI.getInstance().signOut(context!!)
    }

    /** Create a user in firestore if the user does not exist so if we do not find his id **/
    fun createUserInFirestore() {
        val currentUser = getCurrentUser()
        val urlDefaultPicture =
            "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_1280.png"
        if (currentUser != null) {
            val uid = currentUser.uid
            val username = currentUser.displayName
            val email = getCurrentUser()!!.email
            val urlPicture =
                if (currentUser.photoUrl != null) currentUser.photoUrl.toString() else urlDefaultPicture
            val userToCreate = User(
                uid,
                username.toString(),
                email.toString(),
                urlPicture,
                "",
                ArrayList()
            )
            getDataOnCurrentUser().addOnSuccessListener { documentSnapshot ->
                val currentUserInFirestore = documentSnapshot.toObject(User::class.java)
                if (currentUserInFirestore == null) {
                    getUsersCollection().document(uid).set(userToCreate)
                }
            }
        }
    }

    /** Get information from the firestore about the current user **/
    private fun getDataOnCurrentUser(): Task<DocumentSnapshot> {
        return getUsersCollection().document(getCurrentUserId()).get()
    }

    /** Get information in real time from the firestore about the current user **/

    private val currentUser = MutableLiveData<User>()

    private fun callListenerOnTheCurrentUserData() {
        getUsersCollection().document(getCurrentUserId()).addSnapshotListener { value, _ ->
            if (value != null) {
                val currentUserInFirestore = value.toObject(User::class.java)
                currentUserInFirestore?.let {
                    currentUser.value = it
                }
            }
        }
    }

    fun getTheCurrentUserData(): LiveData<User> {
        callListenerOnTheCurrentUserData()
        return currentUser
    }

    /** **/

    /** Get all the Users of the app **/

    private val listOfAllUsers = MutableLiveData<List<User>>()

    private fun getAllUsers() {
        getUsersCollection()
            .addSnapshotListener { value: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableListOfAllUsers: MutableList<User> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        if (myUser?.uid != getCurrentUserId()) {
                            myUser?.let { mutableListOfAllUsers.add(it) }
                        }
                    }
                }
                listOfAllUsers.setValue(mutableListOfAllUsers)
            }
    }

    fun getListOfAllUsers(): LiveData<List<User>> {
        getAllUsers()
        return listOfAllUsers
    }

    /** **/

    /** Set or update FCM Token of the current user to allow him to be notified when he receives messages
     *  Add a user to the friend list of the current user if he accept the friend request
     **/
    fun setCurrentUserData(updatedUser: User) {
        getUsersCollection().document(getCurrentUserId()).set(updatedUser)
    }

    /** Set the user data that received a friend request to allow him to accept or refuse the friend request
     *  Add a user to the friends list of the user who sent a friend request if it is accepted
     **/
    fun setUserDataWhoSentFriendRequest(
        uidWhoReceivedFriendRequest: String,
        updatedUser: User
    ) {
        getUsersCollection().document(uidWhoReceivedFriendRequest).set(updatedUser)
    }

    /** Create a friends requests sent Collection to retrieve the list of users to whom the current user has sent a friend request **/
    fun createTheFriendRequestSent(uidWhoReceived: String, userWhoReceived: User) {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_FRIENDS_REQUESTS_SENT
        )
            .document(uidWhoReceived).set(userWhoReceived)
    }

    /** Create a friends requests received Collection to get a list of friends requests that user can consult for accept or refuse them **/
    fun createTheFriendRequestReceived(
        uidWhoReceived: String,
        uidWhoSent: String,
        userWhoSent: User
    ) {
        getUsersCollection().document(uidWhoReceived).collection(
            COLLECTION_FRIENDS_REQUESTS_RECEIVED
        )
            .document(uidWhoSent)
            .set(userWhoSent)
    }

    /** Find a specific user based on what the user typed in the edit text **/

    private val userSearchResult = MutableLiveData<List<User>>()

    fun searchUser(usernameTyped: String) {
        getUsersCollection().orderBy("username").startAt(usernameTyped).endAt(usernameTyped)
            .addSnapshotListener { value: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableUserSearchResult: MutableList<User> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        if (myUser?.uid != getCurrentUserId()) {
                            myUser?.let { mutableUserSearchResult.add(it) }
                        }
                    }
                }
                userSearchResult.setValue(mutableUserSearchResult)
            }
    }

    fun getUserSearchResult(): LiveData<List<User>> {
        return userSearchResult
    }

    /** **/

    /** Get the lists of friends requests of the current user to display it in a recyclerview and enable the current user to accept or refuse them **/

    private val listOfFriendsRequestsReceived = MutableLiveData<List<User>>()

    private fun callListOfFriendsRequestsReceived() {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_FRIENDS_REQUESTS_RECEIVED
        )
            .addSnapshotListener { value, _ ->
                val mutableListOfFriendsRequestsReceived: MutableList<User> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        myUser?.let { mutableListOfFriendsRequestsReceived.add(it) }
                    }
                }
                listOfFriendsRequestsReceived.setValue(mutableListOfFriendsRequestsReceived)
            }
    }

    fun getListOfFriendsRequestsReceived(): LiveData<List<User>> {
        callListOfFriendsRequestsReceived()
        return listOfFriendsRequestsReceived
    }

    private val listOfFriendsRequestsSent = MutableLiveData<List<String>>()

    private fun callListOfFriendsRequestsSent() {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_FRIENDS_REQUESTS_SENT
        )
            .addSnapshotListener { value, _ ->
                val mutableListOfFriendsRequestsSent: MutableList<String> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        myUser?.uid?.let { mutableListOfFriendsRequestsSent.add(it) }
                    }
                }
                listOfFriendsRequestsSent.setValue(mutableListOfFriendsRequestsSent)
            }
    }

    fun getListOfFriendsRequestsSent(): LiveData<List<String>> {
        callListOfFriendsRequestsSent()
        return listOfFriendsRequestsSent
    }

    /** **/

    /** When the current user accept or refuse a friend request we delete that friend request for both users **/
    fun deleteFriendsRequestsWhenItIsProcessed(uid: String, friendId: String) {
        getUsersCollection().document(uid).collection(COLLECTION_FRIENDS_REQUESTS_RECEIVED)
            .document(friendId).delete()
        getUsersCollection().document(friendId).collection(COLLECTION_FRIENDS_REQUESTS_SENT)
            .document(uid).delete()
    }

    /** Create the Last Messages collection so you can sort the current user's list of friends based on the ones they spoke with most recently **/
    fun createLastMessagesSentOrReceived(uid1: String, uid2: String, lastMessage: LastMessage) {
        getUsersCollection().document(uid1).collection(COLLECTION_LAST_MESSAGES).document(uid2)
            .set(lastMessage)
    }

    /** Get the list of id of friends of the current user sorted by the ones they spoke with most recently
        Get the list of the last messages of the user to be able to know if he read them or not **/

    private val listSortedByLastFriendsCurrentUserSpokeWith = MutableLiveData<List<String>>()
    private val listOfLastMessages = MutableLiveData<List<LastMessage>>()

    private fun callListSortedByLastFriendsCurrentUserSpokeWith() {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_LAST_MESSAGES
        ).orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                val mutableListSortedByLastFriendsCurrentUserSpokeWith: MutableList<String> =
                    mutableListOf()
                val mutableListOfLastMessages: MutableList<LastMessage> =
                    mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val lastMessage = document.toObject(LastMessage::class.java)
                        lastMessage?.let {
                            mutableListSortedByLastFriendsCurrentUserSpokeWith.add(
                                it.uid
                            )
                            mutableListOfLastMessages.add(it)
                        }
                    }
                }
                listSortedByLastFriendsCurrentUserSpokeWith.value =
                    mutableListSortedByLastFriendsCurrentUserSpokeWith
                listOfLastMessages.value = mutableListOfLastMessages
            }
    }

    fun getListSortedByLastFriendsCurrentUserSpokeWith(): LiveData<List<String>> {
        callListSortedByLastFriendsCurrentUserSpokeWith()
        return listSortedByLastFriendsCurrentUserSpokeWith
    }

    fun getListOfLastMessages(): MutableLiveData<List<LastMessage>> {
        return listOfLastMessages
    }

    /** **/

    /** Create a collection for save in database the hour of the last opening of the Chat Fragment for know if current user read last messages or not **/

    fun createLastChatFragmentOpening(
        currentUid: String,
        uidOfReceiver: String,
        lastChatFragmentOpening: LastChatFragmentOpening
    ) {
        getUsersCollection().document(currentUid).collection(
            COLLECTION_LAST_CHAT_FRAGMENT_OPENING
        ).document(uidOfReceiver).set(lastChatFragmentOpening)
    }

    private val listOfLastChatFragmentOpening = MutableLiveData<List<LastChatFragmentOpening>>()

    private fun callLastChatFragmentOpening() {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_LAST_CHAT_FRAGMENT_OPENING
        ).addSnapshotListener { value, _ ->
            val mutableListLastChatFragmentOpening: MutableList<LastChatFragmentOpening> =
                mutableListOf()
            if (value != null) {
                for (document in value.documents) {
                    val lastChatFragmentOpening =
                        document.toObject(LastChatFragmentOpening::class.java)
                    lastChatFragmentOpening?.let { mutableListLastChatFragmentOpening.add(it) }
                }
            }
            listOfLastChatFragmentOpening.value = mutableListLastChatFragmentOpening
        }
    }

    fun getListOfLastChatFragmentOpening(): MutableLiveData<List<LastChatFragmentOpening>> {
        callLastChatFragmentOpening()
        return listOfLastChatFragmentOpening
    }

    /** **/

}