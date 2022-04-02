package com.dozmaden.rookievpn.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.databinding.FragmentHomeBinding
import com.dozmaden.rookievpn.dto.NetworkInfo
import com.dozmaden.rookievpn.state.VpnConnectionStatus

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setNetworkInfoObserver()
        setVpnConnectionStatusObserver()
        binding.connectButton.setOnClickListener(this)

        viewModel.checkVpnActivity()

        viewModel.loadNetworkInfo()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when (viewModel.getConnectionStatus()) {
            VpnConnectionStatus.NOT_CONNECTED, VpnConnectionStatus.DISCONNECTED -> {
                connectToVpn()
            }
            VpnConnectionStatus.CONNECTING -> {
                return
            }
            else -> {
                disconnectFromVpn()
            }
        }
        viewModel.loadNetworkInfo()
    }

    private fun setNetworkInfoObserver() {
        viewModel.networkInfo.observe(viewLifecycleOwner) { networkInfo ->
            setNetworkInfo(networkInfo)
        }
    }

    private fun setNetworkInfo(network: NetworkInfo) {
        binding.ipAddress.text = network.ip
        binding.country.text = network.country
        binding.region.text = network.regionName
        binding.isp.text = network.isp
        binding.organization.text = network.org
    }

    private fun setVpnConnectionStatusObserver() {
        viewModel.connectionStatus.observe(viewLifecycleOwner) { connectionStatus ->
            setVpnConnectionStatus(connectionStatus)
            setButtonConnectionStatus(connectionStatus)
        }
    }

    private fun setVpnConnectionStatus(status: VpnConnectionStatus) {
        binding.progressCircle.visibility = View.INVISIBLE
        when (status) {
            VpnConnectionStatus.NOT_CONNECTED -> {
                setNetworkStatusText(
                    "Not connected to a VPN!",
                    Color.parseColor("#ff0000")
                )
            }
            VpnConnectionStatus.CONNECTING -> {
                setNetworkStatusText(
                    "Connecting to VPN...",
                    Color.parseColor("#001eff")
                )
                binding.progressCircle.visibility = View.VISIBLE
            }
            VpnConnectionStatus.CONNECTED -> {
                setNetworkStatusText(
                    "Connected to VPN!",
                    Color.parseColor("#04ff00")
                )
            }
            VpnConnectionStatus.DISCONNECTED -> {
                setNetworkStatusText(
                    "Disconnected from VPN",
                    Color.parseColor("#ffd000")
                )
            }
        }
    }

    private fun setNetworkStatusText(str: String, col: Int) {
        binding.networkStatus.text = str
        binding.networkStatus.setTextColor(col)
    }

    private fun setButtonConnectionStatus(status: VpnConnectionStatus) {
        binding.connectButton.text =
            when (status) {
                VpnConnectionStatus.CONNECTED -> {
                    "Disconnect"
                }
                VpnConnectionStatus.CONNECTING -> {
                    "Connecting..."
                }
                else -> {
                    "Connect"
                }
            }
    }

    private fun connectToVpn() {
        val intent = VpnService.prepare(context)
        if (intent != null) {
            startActivityForResult(intent, 1)
        } else {
            viewModel.startVpn()
        }
    }

    private fun disconnectFromVpn() {
        val builder = AlertDialog.Builder(
            requireActivity()
        )

        builder.setMessage(requireActivity().getString(R.string.connection_close_confirm))

        builder.setPositiveButton(
            requireActivity().getString(R.string.yes)
        ) { _, _ -> viewModel.stopVPN() }
        builder.setNegativeButton(
            requireActivity().getString(R.string.no)
        ) { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                viewModel.updateVpnConnectionStatus(intent.getStringExtra("state"))
                val duration = intent.getStringExtra("duration")
                val lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                val byteIn = intent.getStringExtra("byteIn")
                val byteOut = intent.getStringExtra("byteOut")
                updateConnectionInfo(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateConnectionInfo(
        duration: String?,
        lastPacket: String?,
        byteIn: String?,
        byteOut: String?
    ) {
        if (duration != null && lastPacket != null && byteIn != null && byteOut != null) {
            binding.connectionInfo.visibility = View.VISIBLE
            binding.duration.text = "00:00:00"
            binding.lastPacket.text = "0"
            binding.bytesIn.text = " "
            binding.bytesOut.text = " "
        } else {
            binding.connectionInfo.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        val str = viewModel.getConnectionStatus().toString()
        Log.d("ONRESUME", str)

        viewModel.checkVpnActivity()

        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        val str = viewModel.getConnectionStatus().toString()
        Log.d("ONPAUSE", str)

        viewModel.checkVpnActivity()

        super.onPause()
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        val str = viewModel.getConnectionStatus().toString()
        Log.d("ONSTART", str)

        viewModel.checkVpnActivity()

        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
//        viewModel.stopVPN()
        super.onDestroy()
    }
}