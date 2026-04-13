package com.peter.security.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.security.demo.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bnRelaunch.setOnClickListener {
            binding.tvStatus.text = "当前配置:\n" +
                "- Theme: Theme.SplashScreen (父主题)\n" +
                "- Background: #006B5E (绿色)\n" +
                "- Icon: ic_menu_info_details\n" +
                "- postSplashScreenTheme: Theme.SecurityDemo\n\n" +
                "重新启动 App 即可看到 Splash 效果。\n" +
                "在 Android 12+ 上还会看到系统的 Splash 动画。"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
