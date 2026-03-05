package com.peter.lifecycle.demo.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.peter.lifecycle.demo.R
import com.peter.lifecycle.demo.databinding.ActivityMvvmBinding
import com.peter.lifecycle.demo.databinding.ItemUserBinding

/**
 * MVVM 架构实战示例
 * 
 * 架构层次：
 * 1. View (Activity/Fragment) - UI 展示和用户交互
 * 2. ViewModel - 持有 UI 状态，处理业务逻辑
 * 3. Repository - 数据层，获取和管理数据
 * 4. Model - 数据模型
 * 
 * 数据流向：
 * View -> ViewModel -> Repository -> ViewModel -> View
 * 
 * 特点：
 * - 单向数据流
 * - 生命周期感知
 * - 易于测试
 */
class MvvmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMvvmBinding
    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository())
    }
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMvvmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViews()
        observeData()

        // 初始加载数据
        viewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter { user ->
            viewModel.selectUser(user)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshUsers()
        }
        
        binding.btnRetry.setOnClickListener {
            viewModel.loadUsers()
        }
        
        binding.btnCloseDetail.setOnClickListener {
            viewModel.clearSelection()
        }
    }

    private fun observeData() {
        // 观察用户列表状态
        viewModel.usersState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
        
        // 观察刷新状态
        viewModel.isRefreshing.observe(this) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
        
        // 观察选中用户
        viewModel.selectedUser.observe(this) { user ->
            if (user != null) {
                showUserDetail(user)
            } else {
                hideUserDetail()
            }
        }
    }

    private fun showUserDetail(user: User) {
        binding.detailLayout.visibility = View.VISIBLE
        binding.tvDetailName.text = user.name
        binding.tvDetailEmail.text = user.email
        binding.tvDetailAge.text = "年龄: ${user.age}"
    }

    private fun hideUserDetail() {
        binding.detailLayout.visibility = View.GONE
    }
}

/**
 * 用户列表 Adapter
 */
class UserAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, onClick: (User) -> Unit) {
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.root.setOnClickListener { onClick(user) }
        }
    }
}

/**
 * DiffUtil 回调
 */
class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
