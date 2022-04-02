package com.dozmaden.rookievpn.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dozmaden.rookievpn.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun getApps() {
//        val activity: FragmentActivity = this.requireActivity()
//        val packageManager = activity.packageManager
//        val packageInfoList = packageManager.getInstalledPackages(0)
//        for (info in packageInfoList) {
//            if (!isUserApp(info) || AppConst.APP_PACKAGE_NAME.equals(info.packageName)) {
//                continue
//            }
//            var applicationInfo: ApplicationInfo? = null
//            var icon: Drawable? = null
//            try {
//                applicationInfo = packageManager.getApplicationInfo(info.packageName, 0)
//                icon = applicationInfo.loadIcon(packageManager)
//            } catch (e: PackageManager.NameNotFoundException) {
//                e.printStackTrace()
//            }
//            val name =
//                (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "unknown") as String
//            val app = App(icon, name, info.packageName, bypassApps.contains(info.packageName))
//            appList.add(app)
//        }
//        val arrayAdapter: ArrayAdapter<App> = AppArrayAdapter(this.getActivity(), appList)
//        this.listView.setAdapter(arrayAdapter)
//    }
}