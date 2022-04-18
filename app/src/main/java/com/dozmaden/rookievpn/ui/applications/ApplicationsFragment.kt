package com.dozmaden.rookievpn.ui.applications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.databinding.FragmentApplicationsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ApplicationsFragment : Fragment() {

//    private val viewModel: ApplicationsViewModel by viewModels()
    private lateinit var viewModel: ApplicationsViewModel

    private lateinit var scrollView: NestedScrollView
    private lateinit var content: View
    private lateinit var selectedTitle: TextView
    private lateinit var selectedApps: RecyclerView
    private lateinit var unselectedTitle: TextView
    private lateinit var unselectedApps: RecyclerView

    private var _binding: FragmentApplicationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ApplicationsViewModel::class.java]

        _binding = FragmentApplicationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScrollView(view)
        setupSelectedApps(view)
        setupUnselectedApps(view)
//        setupFocusButton(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupScrollView(view: View) {
        content = view.findViewById(R.id.content)
        scrollView = view.findViewById(R.id.scroll_view)
    }

    private fun setupSelectedApps(view: View) {
        selectedTitle = view.findViewById(R.id.selected_title)
        unselectedTitle = view.findViewById(R.id.unselected_title)
        selectedApps = view.findViewById(R.id.selected_apps)
        val adapter = ApplicationsAdapter()
        adapter.setOnAppClickListener(viewModel::removeFromSelected)
        selectedApps.adapter = adapter
//        blacklistApps.itemAnimator = FadeInUpAnimator()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedAppsFlow.collect {
                    selectedTitle.text = if (it.isEmpty()) "Applications:" else "Your blacklist:"
                    unselectedTitle.isVisible = it.isNotEmpty()
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setupUnselectedApps(view: View) {
        unselectedApps = view.findViewById(R.id.unselected_apps)
        val adapter = ApplicationsAdapter()
        adapter.setOnAppClickListener(viewModel::addToSelected)
        unselectedApps.adapter = adapter
//        allowedApps.itemAnimator = FadeInDownAnimator()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unselectedAppsFlow.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    private fun setupFocusButton(view: View) {
//        focusModeButton = view.findViewById(R.id.focus_mode_button)
//        focusModeButton.setOnClickListener {
//            if (!requireContext().isAccessibilitySettingsOn()) {
//                openAccessibilityService()
//                return@setOnClickListener
//            }
//            val focusModeOn = !viewModel.getFocusModeStatus()
//            viewModel.setFocusModeStatus(focusModeOn)
//            val msg = if (focusModeOn) "FocusMode has been started!" else "FocusMode has been stopped"
//            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//        }
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.blackListAppsFlow.combine(viewModel.focusModeStatusFlow) { blacklist, focusModeOn ->
//                    focusModeButton.isEnabled = if (focusModeOn && requireContext().isAccessibilitySettingsOn()) {
//                        true
//                    } else {
//                        blacklist.isNotEmpty()
//                    }
//                    focusModeButton.text =
//                        if (focusModeOn && requireContext().isAccessibilitySettingsOn())
//                            "Stop FocusMode"
//                        else
//                            "Start FocusMode"
//                }
//                    .flowOn(Dispatchers.Main.immediate)
//                    .collect {}
//            }
//        }
//    }

//    private fun openAccessibilityService() {
//        if (!requireContext().isAccessibilitySettingsOn()) {
//            val dialog = AlertDialog.Builder(requireContext())
//                .setTitle("Accessibility service")
//                .setMessage("Please turn on accessibility service")
//                .setPositiveButton("OK") { _, _ ->
//                    Toast.makeText(
//                        requireContext(),
//                        "Please turn on UsageManager service",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//                }
//            dialog.show()
//        }
//    }
}