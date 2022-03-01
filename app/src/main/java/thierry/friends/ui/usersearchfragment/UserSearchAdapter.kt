package thierry.friends.ui.usersearchfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.friends.databinding.UserItemBinding
import thierry.friends.model.User

class UserSearchAdapter(
    private val userSearchResult: List<User>,
    private val currentUserInFirestore: User,
    private val listOfFriendsRequests: List<String>?,
    private val listOfFriendsRequestsSent: List<String>?,
    callback: OnFriendRequestClicked
) :
    RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {
    private var callback: OnFriendRequestClicked? = callback

    interface OnFriendRequestClicked {
        fun onFriendRequestClicked(userWhoReceivedFriendRequest: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = userSearchResult[position].username
        Glide.with(holder.userPic).load(userSearchResult[position].userPicture).circleCrop()
            .into(holder.userPic)
        if (listOfFriendsRequests!!.contains(userSearchResult[position].uid) || listOfFriendsRequestsSent!!.contains(
                userSearchResult[position].uid
            ) || currentUserInFirestore.listOfFriends.contains(userSearchResult[position].uid)
        ) {
            holder.friendRequest.isVisible = false
        } else {
            holder.friendRequest.setOnClickListener {
                callback?.onFriendRequestClicked(
                    userSearchResult[position]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return userSearchResult.count()
    }

    class ViewHolder(binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userPic = binding.userPic
        val username = binding.username
        val friendRequest = binding.friendRequestButton
    }

}