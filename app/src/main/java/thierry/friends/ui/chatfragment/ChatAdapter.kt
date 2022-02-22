package thierry.friends.ui.chatfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.friends.R
import thierry.friends.databinding.MessageItemBinding
import thierry.friends.model.Message
import java.text.DateFormat

class ChatAdapter(
    private val listOfMessages: List<Message>,
    private val currentUserId: String
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = listOfMessages[position].message
        if (listOfMessages[position].from == currentUserId) {
            holder.message.setBackgroundResource(R.drawable.rounded_message_gray)
        } else {
            holder.message.setBackgroundResource(R.drawable.rounded_message_beige)
        }
        holder.date.text =
            DateFormat.getTimeInstance(DateFormat.SHORT).format(listOfMessages[position].date)
        Glide.with(holder.userPic)
            .load(listOfMessages[position].urlPicFrom)
            .circleCrop().into(holder.userPic)
    }

    override fun getItemCount(): Int {
        return listOfMessages.count()
    }

    class ViewHolder(binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val message = binding.userMessage
        val date = binding.messageDate
        val userPic = binding.userPicture
    }

}