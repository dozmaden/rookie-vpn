package com.dozmaden.rookievpn.ui.servers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.databinding.FragmentServersBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ServersFragment : Fragment() {
    private lateinit var viewModel: ServersViewModel

    private lateinit var scrollView: NestedScrollView
    private lateinit var content: View

    private lateinit var selectedTitle: TextView
    private lateinit var selectedServers: RecyclerView
    private lateinit var availableTitle: TextView
    private lateinit var availableServers: RecyclerView

    private var _binding: FragmentServersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ServersViewModel::class.java]

        _binding = FragmentServersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScrollView(view)
        setupSelectedServers(view)
        setupAvailableServers(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupScrollView(view: View) {
        content = view.findViewById(R.id.servers_content)
        scrollView = view.findViewById(R.id.servers_scroll_view)
    }

    @SuppressLint("SetTextI18n")
    private fun setupSelectedServers(view: View) {
        selectedTitle = view.findViewById(R.id.selected_servers_title)
        availableTitle = view.findViewById(R.id.available_servers_title)
        selectedServers = view.findViewById(R.id.selected_servers)

        val adapter = ServersAdapter()
        adapter.setOnServerClickListener(viewModel::removeFromSelected)
        selectedServers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedServersFlow.collect { it ->
                    selectedTitle.text = if (it.isEmpty()) {
                        "Available Servers:"
                    } else {
                        "Selected server:"
                    }
                    it.isNotEmpty().let {
                        availableTitle.isVisible = it
                        availableTitle.text = "Available Servers:"
                    }
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setupAvailableServers(view: View) {
        availableServers = view.findViewById(R.id.available_servers)

        val adapter = ServersAdapter()
        adapter.setOnServerClickListener(viewModel::addToSelected)
        availableServers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unselectedServersFlow.collect {
                    adapter.submitList(it)
                }
            }
        }
    }
}