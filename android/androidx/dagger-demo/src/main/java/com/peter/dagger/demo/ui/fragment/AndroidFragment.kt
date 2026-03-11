package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.databinding.FragmentAndroidBinding

/**
 * AndroidFragment - Android 集成演示
 *
 * 学习目标：
 * 1. Activity/Fragment 注入
 * 2. ViewModel 手动注入
 * 3. Dagger2 在 Android 中的最佳实践
 */
class AndroidFragment : Fragment() {

    private var _binding: FragmentAndroidBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AndroidFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAndroidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Step 5: Android 集成"
        binding.tvDescription.text = """
            |学习 Dagger2 在 Android 中的应用：
            |
            |1. 手动调用 component.inject(this)
            |2. ViewModel Factory 注入
            |3. 与 Hilt 的对比
        """.trimMargin()

        binding.btnShowAndroid.setOnClickListener {
            showAndroidIntegration()
        }
    }

    private fun showAndroidIntegration() {
        // TODO: 在 Phase 6 实现 Android 集成演示
        binding.tvResult.text = """
            |Android Integration Result:
            |
            |Android 集成将在 Phase 6 实现
            |
            |Dagger2 vs Hilt 注入对比:
            |
            |Dagger2 (手动):
            |(activity as DemoApplication)
            |    .appComponent
            |    .inject(this)
            |
            |Hilt (自动):
            |@AndroidEntryPoint
            |class MainActivity : AppCompatActivity()
        """.trimMargin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
