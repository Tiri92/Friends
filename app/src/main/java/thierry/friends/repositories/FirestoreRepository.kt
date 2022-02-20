package thierry.friends.repositories

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import thierry.friends.model.User
import javax.inject.Singleton
import javax.inject.Inject

private const val COLLECTION_USERS = "users"

@Singleton
class FirestoreRepository @Inject constructor() {

    /** get the users collection **/
    private fun getUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
    }

    /** Get the current firebase user **/
    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    /** Get the id of the current user **/
    private fun getCurrentUserId(): String {
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
                urlPicture
            )
            val userData: Task<DocumentSnapshot> = getDataOnCurrentUser()
            userData.addOnSuccessListener { documentSnapshot ->
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

}