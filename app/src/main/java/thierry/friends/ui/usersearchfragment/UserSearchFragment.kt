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

@AndroidEntryPoint
class UserSearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: UserSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserSearchBinding.inflate(layoutInflater)
        val rootView = binding.root

        recyclerView = binding.recyclerviewUserSearch
        binding.userSearchButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        binding.userSearchButton.setOnClickListener {
            viewModel.searchUser(binding.editTextUserSearch.text.toString())
        }
        viewModel.getTheUserSearchResult().observe(viewLifecycleOwner) { userSearchResult ->
            setUpRecyclerView(recyclerView, userSearchResult)
        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        userSearchResult: List<User>
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = UserSearchAdapter(userSearchResult)
    }

    companion object {
        fun newInstance() = UserSearchFragment()
    }

}