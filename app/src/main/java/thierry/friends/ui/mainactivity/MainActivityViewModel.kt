package thierry.friends.ui.mainactivity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.repositories.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ViewModel() {

    fun isCurrentUserLogged(): Boolean {
        return firestoreRepository.isCurrentUserLogged()
    }

    fun logout(context: Context): Task<Void?> {
        return firestoreRepository.logout(context)
    }

    fun listenerOnTheCurrentUserData(): DocumentReference {
        return firestoreRepository.listenerOnTheCurrentUserData()
    }

}