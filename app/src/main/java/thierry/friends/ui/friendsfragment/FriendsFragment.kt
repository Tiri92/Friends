package thierry.friends.ui.friendsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import thierry.friends.databinding.FragmentFriendsBinding
import thierry.friends.model.User

@AndroidEntryPoint
class FriendsFragment : Fragment() {

    private val viewModel: FriendsFragmentViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFriendsBinding.inflate(layoutInflater)
        val rootView = binding.root

        recyclerView = binding.recyclerviewFriends
        viewModel.getListOfAllFriends().observe(viewLifecycleOwner) { listOfAllUsers ->
            setUpRecyclerView(recyclerView, listOfAllUsers, parentFragmentManager)
        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        listOfAllUsers: List<User>,
        parentFragmentManager: FragmentManager
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter =
            FriendsAdapter(listOfAllUsers, parentFragmentManager)
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}