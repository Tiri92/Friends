package thierry.friends.ui.chatfragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import thierry.friends.R
import thierry.friends.databinding.FragmentChatBinding
import thierry.friends.model.Message
import thierry.friends.model.User
import java.util.*

private const val ARG_UID = "uid"
private const val ARG_USERNAME = "username"

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val viewModel: ChatFragmentViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUserPicture: String
    private lateinit var bottomNav: BottomNavigationView
    private var uidOfReceiver: String? = null
    private var usernameOfReceiver: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uidOfReceiver = it.getString(ARG_UID)
            usernameOfReceiver = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(layoutInflater)
        val rootView = binding.root

        bottomNav = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.isVisible = false

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            getString(R.string.chat_with) + " $usernameOfReceiver"

        recyclerView = binding.recyclerViewChat
        val currentUserId = viewModel.getCurrentUserId()

        viewModel.getMessagesBetweenTwoUsers().observe(viewLifecycleOwner) { listOfMessage ->
            if (!listOfMessage.isNullOrEmpty()) {
                setUpRecyclerView(recyclerView, listOfMessage, currentUserId)
                recyclerView.scrollToPosition(listOfMessage.lastIndexOf(listOfMessage.last()))
            }
        }
        viewModel.callMessagesBetweenTwoUsers(currentUserId, uidOfReceiver.toString())

        viewModel.listenerOnTheCurrentUserData().addSnapshotListener { value, _ ->
            if (value != null) {
                val currentUserInFirestore = value.toObject(User::class.java)
                if (currentUserInFirestore != null) {
                    currentUserPicture = currentUserInFirestore.userPicture
                }
            }
        }

        binding.sendMessageButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))

        binding.sendMessageButton.setOnClickListener {
            if (binding.editTextMessage.text.toString().isNotEmpty()) {
                val date = Date()
                val message = Message(
                    currentUserId,
                    uidOfReceiver.toString(),
                    binding.editTextMessage.text.toString(),
                    date,
                    Arrays.asList(currentUserId, uidOfReceiver), currentUserPicture
                )
                viewModel.createNewMessage(message)
                binding.editTextMessage.setText("")
            }
        }

        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.logout).isVisible = false
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        listOfMessages: List<Message>,
        currentUserId: String
    ) {
        recyclerView.setHasFixedSize(true)
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = ChatAdapter(listOfMessages, currentUserId)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanUpTheLiveData()
        bottomNav.isVisible = true
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Friends"
    }

    companion object {
        fun newInstance(uid: String, username: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                    putString(ARG_USERNAME, username)
                }
            }
    }

}