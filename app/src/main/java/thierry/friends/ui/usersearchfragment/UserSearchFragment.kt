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
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserSearchBinding.inflate(layoutInflater)
        val rootView = binding.root

        viewModel.getViewState().observe(viewLifecycleOwner) { userSearchViewState ->

            recyclerView = binding.recyclerviewUserSearch

            if (userSearchViewState.currentUser != null) {
                this.currentUser = userSearchViewState.currentUser
                userSearchViewState.userSearchResult?.let { userSearchResult ->
                    setUpRecyclerView(
                        recyclerView,
                        userSearchResult,
                        userSearchViewState.currentUser!!,
                        userSearchViewState.listOfFriendsRequestsReceivedById,
                        userSearchViewState.listOfFriendsRequestsSentById
                    )
                }
            }

            binding.userSearchButton.imageTintList =
                ColorStateList.valueOf(Color.rgb(255, 255, 255))
            binding.userSearchButton.setOnClickListener {
                viewModel.searchUser(binding.editTextUserSearch.text.toString())
            }

            if (userSearchViewState.userSearchResult != null) {
                userSearchViewState.currentUser?.let {
                    setUpRecyclerView(
                        recyclerView, userSearchViewState.userSearchResult!!,
                        it,
                        userSearchViewState.listOfFriendsRequestsReceivedById,
                        userSearchViewState.listOfFriendsRequestsSentById
                    )
                }
                if (userSearchViewState.listOfFriendsRequestsSentById != null) {
                    setUpRecyclerView(
                        recyclerView,
                        userSearchViewState.userSearchResult!!,
                        userSearchViewState.currentUser!!,
                        userSearchViewState.listOfFriendsRequestsReceivedById,
                        userSearchViewState.listOfFriendsRequestsSentById
                    )
                }
            }

            recyclerViewForFriendsRequests = binding.recyclerviewFriendsRequests
            if (userSearchViewState.listOfFriendsRequestsReceived != null) {
                setUpRecyclerViewForFriendsRequests(
                    recyclerViewForFriendsRequests,
                    userSearchViewState.listOfFriendsRequestsReceived!!
                )
                if (!userSearchViewState.userSearchResult.isNullOrEmpty()) {
                    setUpRecyclerView(
                        recyclerView,
                        userSearchViewState.userSearchResult!!,
                        userSearchViewState.currentUser!!,
                        userSearchViewState.listOfFriendsRequestsReceivedById,
                        userSearchViewState.listOfFriendsRequestsSentById
                    )
                }
            }

        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        userSearchResult: List<User>,
        currentUser: User,
        listOfFriendsRequestsReceivedById: List<String>?,
        listOfFriendsRequestsSentById: List<String>?
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter =
            UserSearchAdapter(
                userSearchResult,
                currentUser,
                listOfFriendsRequestsReceivedById,
                listOfFriendsRequestsSentById,
                this
            )
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
        viewModel.createTheFriendRequestSent(
            userWhoReceivedFriendRequest.uid,
            userWhoReceivedFriendRequest
        )
        currentUser?.let { currentUser ->
            viewModel.createTheFriendRequestReceived(
                userWhoReceivedFriendRequest.uid,
                currentUser.uid,
                currentUser
            )
        }
        val fcmNotification = FcmNotificationsSender(
            userWhoReceivedFriendRequest.userFcmToken,
            "Friend request",
            "from ${currentUser?.username}",
            requireActivity()
        )
        fcmNotification.sendNotification()
    }

    override fun friendRequestAccepted(newFriend: User) {
        val fcmNotification = FcmNotificationsSender(
            newFriend.userFcmToken,
            "Friend request Accepted !",
            "${currentUser?.username} has accepted your friend request !",
            requireActivity()
        )
        fcmNotification.sendNotification()
        currentUser?.listOfFriends?.add(newFriend.uid)
        currentUser?.let { currentUser -> viewModel.setCurrentUserData(currentUser) }
        currentUser?.uid?.let { currentUser -> newFriend.listOfFriends.add(currentUser) }
        viewModel.setUserDataWhoSentFriendRequest(newFriend.uid, newFriend)
        viewModel.deleteFriendsRequestsWhenItIsProcessed(currentUser!!.uid, newFriend.uid)
    }

    override fun friendRequestRefused(userWhoRefused: User) {
        viewModel.deleteFriendsRequestsWhenItIsProcessed(currentUser!!.uid, userWhoRefused.uid)
    }

    companion object {
        fun newInstance() = UserSearchFragment()
    }

}