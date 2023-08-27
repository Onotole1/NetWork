package ru.netology.network.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.adapter.OnInteractionWallListener
import ru.netology.network.adapter.PostWallAdapter
import ru.netology.network.R
import ru.netology.network.activity.FeedJobFragment.Companion.user_Id
import ru.netology.network.activity.ImagePreviewFragment.Companion.textArg
import ru.netology.network.activity.MapsPreviewFragment.Companion.doubleArg1
import ru.netology.network.activity.MapsPreviewFragment.Companion.doubleArg2
import ru.netology.network.databinding.FragmentWallBinding
import ru.netology.network.dto.Post
import ru.netology.network.util.LongArg
import ru.netology.network.util.StringArg
import ru.netology.network.viewmodel.PostViewModel

@AndroidEntryPoint
class WallFragment : Fragment() {
    companion object {
        var Bundle.userId: Long by LongArg
        var Bundle.userName: String? by StringArg
        var Bundle.userPosition: String? by StringArg
        var Bundle.userAvatar: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWallBinding.inflate(inflater, container, false)

        val currentUser = viewModel.getCurrentUser()

        val userId = (arguments?.userId ?: 0).toLong()
        val userName = arguments?.userName
        val userPosition = arguments?.userPosition
        val userAvatar = arguments?.userAvatar

        if (currentUser == userId) {
            binding.fab.visibility = View.VISIBLE
            binding.avatar.visibility = View.GONE
            binding.author.visibility = View.GONE
            binding.authorJob.visibility = View.GONE
        } else {
            binding.fab.visibility = View.GONE
            binding.avatar.visibility = View.VISIBLE
            binding.author.visibility = View.VISIBLE


            if (userAvatar != null) {
                Glide.with(this)
                    .load(userAvatar)
                    .circleCrop()
                    .placeholder(R.drawable.avatar)
                    .into(binding.avatar)
            } else {
                binding.avatar.setImageResource(R.drawable.avatar)
            }


            binding.author.text = userName

            if (userPosition.isNullOrBlank()) {
                binding.authorJob.visibility = View.GONE
            } else {
                binding.authorJob.text = userPosition
                binding.authorJob.visibility = View.VISIBLE
            }
        }

        val adapter = PostWallAdapter(object : OnInteractionWallListener {
            override fun onEdit(post: Post) {
                viewModel.editPosts(post)
                findNavController().navigate(R.id.action_wallFragment_to_newPostFragment)
            }

            override fun onLike(post: Post) {
                viewModel.likePostById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removePostById(post.id)

            }

            override fun onPreviewMap(post: Post) {
                if (post.coords?.lat != null && post.coords.long != null) {
                    findNavController().navigate(
                        R.id.action_wallFragment_to_mapsPreviewFragment,
                        Bundle().apply {
                            doubleArg1 = post.coords.lat.toDouble()
                            doubleArg2 = post.coords.long.toDouble()
                        })
                }
            }

            override fun onPreviewImage(post: Post) {
                findNavController().navigate(
                    R.id.action_postFeedFragment_to_imagePreviewFragment,
                    Bundle().apply {
                        textArg = post.attachment?.url
                    }
                )
            }
        })
        binding.list.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }
        viewModel.dataPosts.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts.filter {
                it.authorId == userId
            })
            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_wallFragment_to_newPostFragment)//action_wallFragment_to_newPostFragment)
        }

        binding.author.setOnClickListener {
            findNavController().navigate(
                R.id.action_wallFragment_to_feedJobsFragment,
                Bundle().apply {
                    user_Id = userId
                })

        }

        binding.avatar.setOnClickListener {
            findNavController().navigate(
                R.id.action_wallFragment_to_feedJobsFragment,
                Bundle().apply {
                    user_Id = userId
                })

        }

        return binding.root
    }
}
