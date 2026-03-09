package com.peter.notification.demo.fragments

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.notification.demo.databinding.FragmentSoundBinding
import com.peter.notification.demo.databinding.ItemRingtoneBinding
import com.peter.notification.demo.sound.RingtoneManager as AppRingtoneManager

/**
 * 铃声设置 Fragment
 */
class SoundFragment : Fragment() {

    private var _binding: FragmentSoundBinding? = null
    private val binding get() = _binding!!

    private lateinit var ringtoneManager: AppRingtoneManager
    private var selectedUri: Uri? = null
    private lateinit var adapter: RingtoneAdapter

    companion object {
        fun newInstance() = SoundFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ringtoneManager = AppRingtoneManager(requireContext())
        
        setupUI()
        loadRingtones()
    }

    private fun setupUI() {
        binding.btnPlayPreview.setOnClickListener {
            selectedUri?.let { uri ->
                ringtoneManager.playRingtone(uri)
                Toast.makeText(requireContext(), "正在播放预览", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(requireContext(), "请先选择铃声", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnStop.setOnClickListener {
            ringtoneManager.stopCurrentRingtone()
        }
    }

    private fun loadRingtones() {
        val ringtones = ringtoneManager.getAllRingtones()
        
        // 设置默认选中
        selectedUri = ringtoneManager.getDefaultNotificationUri()
        
        adapter = RingtoneAdapter(ringtones, selectedUri) { ringtoneInfo ->
            selectedUri = ringtoneInfo.uri
            binding.tvCurrentRingtone.text = "当前铃声: ${ringtoneInfo.name}"
            adapter.updateSelected(ringtoneInfo.uri)
        }

        binding.recyclerView.apply {
            this.adapter = this@SoundFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 显示当前铃声
        val defaultName = ringtones.find { it.uri == selectedUri }?.name ?: "默认铃声"
        binding.tvCurrentRingtone.text = "当前铃声: $defaultName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ringtoneManager.release()
        _binding = null
    }

    /**
     * 铃声列表适配器
     */
    inner class RingtoneAdapter(
        private var items: List<AppRingtoneManager.RingtoneInfo>,
        private var selectedUri: Uri?,
        private val onSelected: (AppRingtoneManager.RingtoneInfo) -> Unit
    ) : RecyclerView.Adapter<RingtoneAdapter.ViewHolder>() {

        inner class ViewHolder(
            private val binding: ItemRingtoneBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: AppRingtoneManager.RingtoneInfo) {
                binding.apply {
                    tvRingtoneName.text = item.name
                    if (item.isCustom) {
                        tvRingtoneName.text = "${item.name} (自定义)"
                    }
                    
                    radioButton.isChecked = item.uri == selectedUri

                    radioButton.setOnClickListener {
                        onSelected(item)
                    }

                    root.setOnClickListener {
                        onSelected(item)
                    }

                    btnPlay.setOnClickListener {
                        ringtoneManager.playRingtone(item.uri)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemRingtoneBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        fun updateSelected(uri: Uri) {
            selectedUri = uri
            notifyDataSetChanged()
        }
    }
}
