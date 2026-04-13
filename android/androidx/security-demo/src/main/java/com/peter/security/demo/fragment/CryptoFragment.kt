package com.peter.security.demo.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.peter.security.demo.databinding.FragmentCryptoBinding

class CryptoFragment : Fragment() {

    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding!!
    private lateinit var encryptedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEncryptedPrefs()

        binding.btnSave.setOnClickListener { saveEncrypted() }
        binding.btnRead.setOnClickListener { readEncrypted() }
        binding.btnList.setOnClickListener { listAll() }
    }

    private fun initEncryptedPrefs() {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            requireContext(),
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        binding.tvStatus.text = "EncryptedSharedPreferences 初始化成功\n" +
            "MasterKey scheme: AES256_GCM\n" +
            "Key encryption: AES256_SIV\n" +
            "Value encryption: AES256_GCM"
    }

    private fun saveEncrypted() {
        val key = binding.etKey.text?.toString()?.trim().orEmpty()
        val value = binding.etValue.text?.toString()?.trim().orEmpty()
        if (key.isEmpty() || value.isEmpty()) {
            binding.tvStatus.text = "Key 和 Value 不能为空"
            return
        }
        encryptedPrefs.edit().putString(key, value).apply()
        binding.tvStatus.text = "已加密存储:\n  key = \"$key\"\n  value = \"$value\"\n\n" +
            "文件位于: shared_prefs/secret_shared_prefs.xml\n" +
            "内容已加密，直接查看文件无法读取原文。"
    }

    private fun readEncrypted() {
        val key = binding.etKey.text?.toString()?.trim().orEmpty()
        if (key.isEmpty()) {
            binding.tvStatus.text = "Key 不能为空"
            return
        }
        val value = encryptedPrefs.getString(key, null)
        if (value != null) {
            binding.tvStatus.text = "解密读取成功:\n  key = \"$key\"\n  value = \"$value\""
        } else {
            binding.tvStatus.text = "未找到 key=\"$key\" 的存储值"
        }
    }

    private fun listAll() {
        val all = encryptedPrefs.all
        if (all.isEmpty()) {
            binding.tvStatus.text = "当前没有存储任何加密键值"
            return
        }
        val sb = StringBuilder("所有加密键值对 (${all.size} 项):\n\n")
        all.forEach { (k, v) ->
            sb.append("  \"$k\" = \"$v\"\n")
        }
        binding.tvStatus.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
