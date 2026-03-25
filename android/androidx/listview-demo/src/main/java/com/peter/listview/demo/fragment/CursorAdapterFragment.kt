package com.peter.listview.demo.fragment

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.peter.listview.demo.R
import com.peter.listview.demo.adapter.CustomCursorAdapter
import com.peter.listview.demo.databinding.FragmentCursorAdapterBinding
import com.peter.listview.demo.helper.DatabaseHelper
import com.peter.listview.demo.viewmodel.ListViewDemoViewModel

/**
 * Tab 3: CursorAdapter 演示
 *
 * 展示内容：
 * 1. CursorAdapter 基础 - SQLite 数据绑定
 * 2. 异步加载 - Coroutines + LiveData
 * 3. 搜索过滤 - SearchView 配合
 * 4. 数据 CRUD 操作
 */
@Suppress("DEPRECATION")
class CursorAdapterFragment : Fragment() {

    private var _binding: FragmentCursorAdapterBinding? = null
    private val viewModel: ListViewDemoViewModel by activityViewModels()
    private lateinit var databaseHelper: DatabaseHelper
    private var cursorAdapter: CustomCursorAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCursorAdapterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())
        viewModel.setDatabaseHelper(databaseHelper)
        setupSearchView()
        setupListView()
        observeData()
    }

    private fun setupSearchView() {
        _binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 提交搜索
                viewModel.searchUsers(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 实时搜索（可选，可能有性能问题）
                // viewModel.searchUsers(newText ?: "")
                return false
            }
        })
    }

    private fun setupListView() {
        _binding?.listView?.emptyView = _binding?.emptyView?.root

        _binding?.listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            cursorAdapter?.let { adapter ->
                val cursor = adapter.getItem(position) as? Cursor
                cursor?.let {
                    val nameIndex = it.getColumnIndex(DatabaseHelper.COLUMN_NAME)
                    val phoneIndex = it.getColumnIndex(DatabaseHelper.COLUMN_PHONE)

                    val name = it.getString(nameIndex)
                    val phone = it.getString(phoneIndex)

                    Toast.makeText(context, "$name: $phone", Toast.LENGTH_SHORT).show()
                }
            }
        }

        _binding?.listView?.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            // 长按删除
            cursorAdapter?.let { adapter ->
                val cursor = adapter.getItem(position) as? Cursor
                cursor?.let {
                    val idIndex = it.getColumnIndex(DatabaseHelper.COLUMN_ID)
                    val id = it.getLong(idIndex)

                    // 删除联系人
                    viewModel.deleteUser(id)
                    Toast.makeText(context, R.string.delete_contact, Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun observeData() {
        // 加载初始数据
        viewModel.loadUserCursor()

        // 观察 Cursor 变化
        viewModel.userCursor.observe(viewLifecycleOwner) { cursor ->
            if (cursorAdapter == null) {
                cursorAdapter = CustomCursorAdapter(requireContext(), cursor)
                _binding?.listView?.adapter = cursorAdapter
            } else {
                cursorAdapter?.swapCursor(cursor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 关闭 Cursor
        cursorAdapter?.cursor?.close()
        _binding = null
    }
}
