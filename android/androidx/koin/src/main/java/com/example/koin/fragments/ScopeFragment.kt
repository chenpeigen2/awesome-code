package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentScopeBinding
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named

class ScopeFragment : Fragment() {

    private var _binding: FragmentScopeBinding? = null
    private val binding get() = _binding!!

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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Activity Scope",
                description = "Activity级别的Koin作用域，Activity销毁时自动关闭",
                codeSnippet = "scope<MainActivity> {\n  scoped { Service() }\n}",
                category = KoinCategory.SCOPES,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Activity Scope:\n在MainActivity实现AndroidScopeComponent\n即可使用activityScope()",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Scope Linking",
                description = "链接两个作用域共享依赖",
                codeSnippet = "scope.linkTo(otherScope)",
                category = KoinCategory.SCOPES,
                action = {
                    val scope1 = getKoin().createScope("scope_link_1", named("link"))
                    val scope2 = getKoin().createScope("scope_link_2", named("link"))
                    scope1.linkTo(scope2)
                    Toast.makeText(
                        requireContext(),
                        "Scope Linking:\nscope1.linkTo(scope2)\n现在scope1可以访问scope2的依赖",
                        Toast.LENGTH_LONG
                    ).show()
                    scope1.close()
                    scope2.close()
                }
            ),
            KoinItem(
                title = "Scope Source",
                description = "获取作用域的源对象",
                codeSnippet = "scope.getScopeSource<Activity>()",
                category = KoinCategory.SCOPES,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Scope Source:\n通过scopeSource获取声明scope的组件\n如: scopeSource<MainActivity>()",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Close Scope",
                description = "手动关闭作用域释放资源",
                codeSnippet = "scope.close()",
                category = KoinCategory.SCOPES,
                action = {
                    val scope = getKoin().createScope("temp_scope", named("temp"))
                    Toast.makeText(
                        requireContext(),
                        "Close Scope:\n创建scope: temp_scope\n调用scope.close()释放",
                        Toast.LENGTH_LONG
                    ).show()
                    scope.close()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}