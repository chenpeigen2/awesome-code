package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentQualifierBinding
import com.peter.dagger.demo.qualifier.DataSource
import javax.inject.Inject
import javax.inject.Named

/**
 * QualifierFragment - 限定符演示
 */
class QualifierFragment : Fragment() {

    private var _binding: FragmentQualifierBinding? = null
    private val binding get() = _binding!!

    @Inject
    @Named("local")
    lateinit var localDataSource: DataSource

    @Inject
    @Named("remote")
    lateinit var remoteDataSource: DataSource

    companion object {
        fun newInstance() = QualifierFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQualifierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnGetLocal.setOnClickListener {
            showLocalDataSource()
        }

        binding.btnGetRemote.setOnClickListener {
            showRemoteDataSource()
        }
    }

    private fun showLocalDataSource() {
        val sb = StringBuilder()

        sb.appendLine("===== @Named(\"local\") =====")
        sb.appendLine()
        sb.appendLine("类型: ${localDataSource.getSourceType()}")
        sb.appendLine("实例ID: ${localDataSource.getInstanceId()}")
        sb.appendLine("数据: ${localDataSource.fetchData()}")
        sb.appendLine()
        sb.appendLine("hashCode: ${localDataSource.hashCode()}")

        binding.tvResult.text = sb.toString()
    }

    private fun showRemoteDataSource() {
        val sb = StringBuilder()

        sb.appendLine("===== @Named(\"remote\") =====")
        sb.appendLine()
        sb.appendLine("类型: ${remoteDataSource.getSourceType()}")
        sb.appendLine("实例ID: ${remoteDataSource.getInstanceId()}")
        sb.appendLine("数据: ${remoteDataSource.fetchData()}")
        sb.appendLine()
        sb.appendLine("hashCode: ${remoteDataSource.hashCode()}")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
