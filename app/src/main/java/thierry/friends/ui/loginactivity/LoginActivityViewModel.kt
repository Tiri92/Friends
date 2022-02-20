package thierry.friends.ui.loginactivity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.repositories.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ViewModel() {

    fun isCurrentUserLogged(): Boolean {
        return firestoreRepository.isCurrentUserLogged()
    }

    fun createUserInFirestore() {
        firestoreRepository.createUserInFirestore()
    }

}