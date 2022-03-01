package thierry.friends.ui.usersearchfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.friends.databinding.FriendRequestItemBinding
import thierry.friends.model.User

class FriendRequestAdapter(
    private val userSearchResult: List<User>,
    callback: OnFriendRequestResponse
) :
    RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {
    private var callback: OnFriendRequestResponse? = callback

    interface OnFriendRequestResponse {
        fun friendRequestAccepted(newFriend: User)
        fun friendRequestRefused(userWhoRefused: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FriendRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = userSearchResult[position].username
        Glide.with(holder.userPic).load(userSearchResult[position].userPicture).circleCrop()
            .into(holder.userPic)
        holder.acceptFriendRequestButton.setOnClickListener {
            callback?.friendRequestAccepted(userSearchResult[position])
        }

        holder.refuseFriendRequestButton.setOnClickListener {
            callback?.friendRequestRefused(userSearchResult[position])
        }
    }

    override fun getItemCount(): Int {
        return userSearchResult.count()
    }

    class ViewHolder(binding: FriendRequestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userPic = binding.userPic
        val username = binding.username
        val acceptFriendRequestButton = binding.acceptFriendRequestButton
        val refuseFriendRequestButton = binding.refuseFriendRequestButton
    }

}