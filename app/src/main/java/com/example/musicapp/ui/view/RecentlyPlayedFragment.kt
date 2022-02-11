package com.example.musicapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.FragmentNotificationsBinding
import com.example.musicapp.ui.adapter.HomeListAdapter
import com.example.musicapp.ui.viewmodel.RecentlyPlayedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentlyPlayedFragment : Fragment() {

    private val recentlyPlayedViewModel by viewModel<RecentlyPlayedViewModel>()
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

      /*  val textView: TextView = binding.textNotifications
        recentlyPlayedViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentlyPlayedViewModel.fetchRecentlyPlayed();
        addObserver();
    }

    private fun addObserver() {
        recentlyPlayedViewModel.recentlyPlayedDataLivaData.observe(this, Observer {

            it.let {
                binding.recyclerview.adapter = HomeListAdapter(it);
                binding.recyclerview.layoutManager = LinearLayoutManager(this.context);
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}