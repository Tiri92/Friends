package thierry.friends.ui.friendsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.friends.model.User
import thierry.friends.repositories.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class FriendsFragmentViewModel @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ViewModel() {

    fun getListOfAllUsers(): LiveData<List<User>> {
        return firestoreRepository.getListOfAllUsers()
    }

}