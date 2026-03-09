package com.peter.notification.demo.fragments

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager as SystemRingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.notification.demo.R
import com.peter.notification.demo.databinding.FragmentSoundBinding

/**
 * 铃声设置 Fragment
 */
class SoundFragment : Fragment() {

    private var _binding: FragmentSoundBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var currentRingtoneName: String = "默认铃声"

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
        setupViews()
    }

    private fun setupViews() {
        binding.tvCurrentRingtone.text = currentRingtoneName

        binding.btnPreview.setOnClickListener {
            playDefaultNotificationSound()
        }

        setupRingtoneList()
    }

    private fun setupRingtoneList() {
        val ringtones = getSystemRingtones()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RingtoneAdapter(ringtones) { ringtone ->
                currentRingtoneName = ringtone
                binding.tvCurrentRingtone.text = ringtone
                playDefaultNotificationSound()
            }
        }
    }

    private fun getSystemRingtones(): List<String> {
        val ringtones = mutableListOf<String>()
        try {
            val manager = SystemRingtoneManager(requireContext())
            val cursor = manager.cursor
            while (cursor.moveToNext()) {
                val title = cursor.getString(
                    SystemRingtoneManager.TITLE_COLUMN_INDEX
                )
                ringtones.add(title)
            }
        } catch (e: Exception) {
            // Fallback to default list
            ringtones.addAll(listOf(
                "默认铃声",
                "Chime",
                "Ding",
                "Notify",
                "Pebble",
                "Popcorn"
            ))
        }
        return ringtones
    }

    private fun playDefaultNotificationSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
                setDataSource(
                    requireContext(),
                    SystemRingtoneManager.getDefaultUri(SystemRingtoneManager.TYPE_NOTIFICATION)
                )
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }

    inner class RingtoneAdapter(
        private val items: List<String>,
        private val onItemClick: (String) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<RingtoneAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvRingtoneName)
            val type: TextView = view.findViewById(R.id.tvRingtoneType)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ringtone, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = items[position]
            holder.type.text = "系统铃声"
            holder.itemView.setOnClickListener {
                onItemClick(items[position])
            }
        }

        override fun getItemCount(): Int = items.size
    }
}