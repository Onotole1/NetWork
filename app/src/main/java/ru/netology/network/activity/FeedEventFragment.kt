package ru.netology.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.MapsPreviewFragment.Companion.doubleArg1
import ru.netology.network.MapsPreviewFragment.Companion.doubleArg2
import ru.netology.network.databinding.FragmentEventFeedBinding
import ru.netology.network.dto.Event
import ru.netology.network.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedEventFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventFeedBinding.inflate(inflater, container, false)
        val adapter = EventAdapter(object : OnInteractionEventListener {
            override fun onEdit(event: Event) {
                viewModel.editEvent(event)
                findNavController().navigate(R.id.action_postFeedFragment_to_newPostFragment)
            }

            override fun onLike(event: Event) {
                viewModel.likeEventById(event.id, event.likedByMe)
            }

            override fun onRemove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            override fun onParticipate(event: Event) {
                viewModel.participate(event.id, event.participatedByMe)
            }

            override fun onPreviewMap(event: Event) {
                if (event.coords != null && event.coords.lat != null && event.coords.long != null) {
                    findNavController().navigate(R.id.action_feedEventFragment_to_mapsPreviewFragment,
                        Bundle().apply
                        {
                            doubleArg1 = event.coords.lat.toDouble()
                            doubleArg2 = event.coords.long.toDouble()
                        })
                }
            }
        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_load, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadEvents() }
                    .show()
            }
        }
        viewModel.dataEvents.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.events)
            binding.emptyText.isVisible = state.empty
        }
        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshEvents()
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedEventFragment_to_newEventFragment)
        }
        return binding.root
    }
}