package thierry.friends.ui.friendsfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.friends.databinding.UserItemBinding
import thierry.friends.model.User

class FriendsAdapter(private val listOfAllUsers: List<User>) :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = listOfAllUsers[position].username
        Glide.with(holder.userPic).load(listOfAllUsers[position].userPicture).circleCrop()
            .into(holder.userPic)
    }

    override fun getItemCount(): Int {
        return listOfAllUsers.count()
    }

    class ViewHolder(binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userPic = binding.userPic
        val username = binding.username
    }

}