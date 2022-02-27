package thierry.friends.repositories.firestore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import thierry.friends.model.User
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_USERS = "users"
private const val COLLECTION_FRIENDS_REQUESTS_SENT = "friendsRequestsSent"
private const val COLLECTION_FRIENDS_REQUESTS_RECEIVED = "friendsRequestsReceived"
private const val COLLECTION_FRIENDS = "friends"

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
                ArrayList(),
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
    fun listenerOnTheCurrentUserData(): DocumentReference {
        return getUsersCollection().document(getCurrentUserId())
    }

    /** Get all the friends of the current user **/

    private val listOfAllFriends = MutableLiveData<List<User>>()

    private fun getAllFiends() {
        getUsersCollection().document(getCurrentUserId()).collection(COLLECTION_FRIENDS)
            .addSnapshotListener { value: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableListOfAllFriends: MutableList<User> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        if (myUser?.uid != getCurrentUserId()) {
                            myUser?.let { mutableListOfAllFriends.add(it) }
                        }
                    }
                }
                listOfAllFriends.setValue(mutableListOfAllFriends)
            }
    }

    fun getListOfAllFriends(): LiveData<List<User>> {
        getAllFiends()
        return listOfAllFriends
    }

    /** **/

    /** Set or update FCM Token of the current user to allow him to be notified when he receives messages **/
    fun setCurrentUserData(updatedUser: User) {
        getUsersCollection().document(getCurrentUserId()).set(updatedUser)
    }

    /** Set the user data that received a friend request to allow him to accept or refuse the friend request **/
    fun setUserDataWhoReceivedFriendRequest(
        uidWhoReceivedFriendRequest: String,
        updatedUser: User
    ) {
        getUsersCollection().document(uidWhoReceivedFriendRequest).set(updatedUser)
    }

    /** Create a friends requests sent Collection to retrieve the list of users to whom the current user has sent a friend request **/
    fun createFriendsRequestsSentCollection(uidWhoReceived: String, userWhoReceived: User) {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_FRIENDS_REQUESTS_SENT
        )
            .document(uidWhoReceived).set(userWhoReceived)
    }

    /** Create a friends requests received Collection to get a list of friends requests that user can consult for accept or refuse them **/
    fun createFriendsRequestsReceivedCollection(
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

    fun getTheUserSearchResult(): LiveData<List<User>> {
        return userSearchResult
    }

    /** **/

    /** Get the list of friends requests of the current user to display it in a recyclerview and enable the current user to accept or refuse them **/

    private val listOfFriendsRequests = MutableLiveData<List<User>>()

    private fun callListOfFriendsRequests() {
        getUsersCollection().document(getCurrentUserId()).collection(
            COLLECTION_FRIENDS_REQUESTS_RECEIVED
        )
            .addSnapshotListener { value, _ ->
                val mutableListOfFriendsRequests: MutableList<User> = mutableListOf()
                if (value != null) {
                    for (document in value.documents) {
                        val myUser = document.toObject(User::class.java)
                        myUser?.let { mutableListOfFriendsRequests.add(it) }
                    }
                }
                listOfFriendsRequests.setValue(mutableListOfFriendsRequests)
            }
    }

    fun getListOfFriendsRequests(): LiveData<List<User>> {
        callListOfFriendsRequests()
        return listOfFriendsRequests
    }

    /** **/

    /** Assign an empty list to the livedata of the UserSearchFragment when we pass in the onDestroy to avoid that the livedata display her last value when we come back to the fragment **/
    fun cleanUpTheLiveData() {
        userSearchResult.value = mutableListOf()
        listOfFriendsRequests.value = mutableListOf()
    }

    /** When the current user accept a friend request we add the user who sent him this friend request to the list of friends of the current user **/
    fun addingUserToFriendsCollection(uid: String, friendId: String, friend: User) {
        getUsersCollection().document(uid).collection(COLLECTION_FRIENDS).document(friendId)
            .set(friend)
        getUsersCollection().document(uid).collection(COLLECTION_FRIENDS_REQUESTS_RECEIVED)
            .document(friendId).delete()
        getUsersCollection().document(uid).collection(COLLECTION_FRIENDS_REQUESTS_SENT)
            .document(friendId).delete()
    }

}