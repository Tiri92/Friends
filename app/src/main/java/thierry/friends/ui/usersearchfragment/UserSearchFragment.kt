package thierry.friends.ui.usersearchfragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import thierry.friends.databinding.FragmentUserSearchBinding
import thierry.friends.model.User
import thierry.friends.service.FcmNotificationsSender

@AndroidEntryPoint
class UserSearchFragment : UserSearchAdapter.OnFriendRequestClicked,
    FriendRequestAdapter.OnFriendRequestResponse, Fragment() {

    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewForFriendsRequests: RecyclerView
    private var currentUserInFirestore: User? = null
    private var userSearchResult: List<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserSearchBinding.inflate(layoutInflater)
        val rootView = binding.root

        viewModel.listenerOnTheCurrentUserData().addSnapshotListener { value, _ ->
            if (value != null) {
                val currentUserInFirestore = value.toObject(User::class.java)
                if (currentUserInFirestore != null) {
                    this.currentUserInFirestore = currentUserInFirestore
                    if (userSearchResult != null) {
                        setUpRecyclerView(
                            recyclerView,
                            userSearchResult!!,
                            currentUserInFirestore
                        )
                    }
                }
            }
        }

        recyclerViewForFriendsRequests = binding.recyclerviewFriendsRequests
        viewModel.getListOfFriendsRequests().observe(viewLifecycleOwner) { listOfFriendsRequests ->
            setUpRecyclerViewForFriendsRequests(
                recyclerViewForFriendsRequests,
                listOfFriendsRequests
            )
        }

        recyclerView = binding.recyclerviewUserSearch
        binding.userSearchButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        binding.userSearchButton.setOnClickListener {
            viewModel.searchUser(binding.editTextUserSearch.text.toString())
        }
        viewModel.getTheUserSearchResult().observe(viewLifecycleOwner) { userSearchResult ->
            this.userSearchResult = userSearchResult
            currentUserInFirestore?.let {
                setUpRecyclerView(
                    recyclerView,
                    userSearchResult,
                    it
                )
            }
        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        userSearchResult: List<User>,
        currentUserInFirestore: User
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter =
            UserSearchAdapter(userSearchResult, currentUserInFirestore, this)
    }

    private fun setUpRecyclerViewForFriendsRequests(
        recyclerView: RecyclerView,
        listOfFriendsRequests: List<User>
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = FriendRequestAdapter(listOfFriendsRequests, this)
    }

    override fun onFriendRequestClicked(userWhoReceivedFriendRequest: User) {
        currentUserInFirestore?.FriendsRequestsSent?.add(userWhoReceivedFriendRequest.uid)
        currentUserInFirestore?.let { viewModel.setCurrentUserData(it) }
        viewModel.createFriendsRequestsSentCollection(
            userWhoReceivedFriendRequest.uid,
            userWhoReceivedFriendRequest
        )
        userWhoReceivedFriendRequest.FriendsRequestsReceived.add(currentUserInFirestore?.uid.toString())
        viewModel.setUserDataWhoReceivedFriendRequest(
            userWhoReceivedFriendRequest.uid,
            userWhoReceivedFriendRequest
        )
        currentUserInFirestore?.let {
            viewModel.createFriendsRequestsReceivedCollection(
                userWhoReceivedFriendRequest.uid,
                it.uid,
                it
            )
        }
        val fcmNotification = FcmNotificationsSender(
            userWhoReceivedFriendRequest.userFcmToken,
            "Friend request",
            "from ${currentUserInFirestore?.username}",
            requireActivity()
        )
        fcmNotification.sendNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanUpTheLiveData()
    }

    companion object {
        fun newInstance() = UserSearchFragment()
    }

    override fun friendRequestAccepted(newFriend: User) {
        val fcmNotification = FcmNotificationsSender(
            newFriend.userFcmToken,
            "Friend request Accepted !",
            "${currentUserInFirestore?.username} has accepted your friend request !",
            requireActivity()
        )
        fcmNotification.sendNotification()
        viewModel.addingUserToFriendsCollection(
            currentUserInFirestore?.uid.toString(),
            newFriend.uid,
            newFriend
        )
        currentUserInFirestore?.let {
            viewModel.addingUserToFriendsCollection(
                newFriend.uid, it.uid,
                it
            )
        }
    }

    override fun friendRequestRefused() {

    }

}