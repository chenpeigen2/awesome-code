package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentScopeBinding
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import javax.inject.Inject

/**
 * ScopeFragment - 作用域管理演示
 */
class ScopeFragment : Fragment() {

    private var _binding: FragmentScopeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var databaseService: DatabaseService

    @Inject
    lateinit var requestService1: RequestService

    companion object {
        fun newInstance() = ScopeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnGetSingleton.setOnClickListener {
            checkSingleton()
        }

        binding.btnGetRequest.setOnClickListener {
            checkNoScope()
        }
    }

    private fun checkSingleton() {
        val sb = StringBuilder()

        sb.appendLine("===== @Singleton 验证 =====")
        sb.appendLine()
        sb.appendLine("DatabaseService 实例:")
        sb.appendLine("  hashCode: ${databaseService.hashCode()}")
        sb.appendLine("  instanceId: ${databaseService.instanceId}")
        sb.appendLine()
        sb.appendLine("多次注入获取的是同一个实例")

        binding.tvResult.text = sb.toString()
    }

    private fun checkNoScope() {
        val component = (requireActivity().application as DemoApplication).appComponent
        val requestService2 = component.requestService()

        val sb = StringBuilder()

        sb.appendLine("===== 无作用域验证 =====")
        sb.appendLine()
        sb.appendLine("RequestService 实例1:")
        sb.appendLine("  hashCode: ${requestService1.hashCode()}")
        sb.appendLine("  instanceId: ${requestService1.instanceId}")
        sb.appendLine()
        sb.appendLine("RequestService 实例2:")
        sb.appendLine("  hashCode: ${requestService2.hashCode()}")
        sb.appendLine("  instanceId: ${requestService2.instanceId}")
        sb.appendLine()
        sb.appendLine("不同实例: ${requestService1 !== requestService2}")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
