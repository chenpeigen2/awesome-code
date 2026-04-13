package com.peter.security.demo.fragment

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.security.demo.databinding.FragmentBiometricBinding
import java.util.concurrent.TimeUnit

class BiometricFragment : Fragment() {

    private var _binding: FragmentBiometricBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBiometricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBiometricStatus()

        binding.btnAuth.setOnClickListener { startAuth() }
        binding.btnAuthCrypto.setOnClickListener { startCryptoAuth() }
    }

    private fun checkBiometricStatus() {
        val biometricManager = BiometricManager.from(requireContext())
        val status = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "BIOMETRIC_SUCCESS: 可以使用生物识别"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "ERROR_NO_HARDWARE: 没有生物识别硬件"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "ERROR_HW_UNAVAILABLE: 硬件不可用"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "ERROR_NONE_ENROLLED: 未录入指纹/面部"
            else -> "UNKNOWN: 未知状态"
        }
        appendLog("状态检查: $status")
    }

    private fun startAuth() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                appendLog("认证错误 [$errorCode]: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                appendLog("认证成功! authType=${result.authenticationType}")
            }

            override fun onAuthenticationFailed() {
                appendLog("认证失败: 未识别的指纹/面部")
            }
        })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("生物识别认证")
            .setSubtitle("请使用指纹或面部识别进行验证")
            .setNegativeButtonText("取消")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        prompt.authenticate(info)
        appendLog("已启动 BiometricPrompt ...")
    }

    private fun startCryptoAuth() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                appendLog("Crypto认证错误 [$errorCode]: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val crypto = result.cryptoObject
                val cipher = crypto?.cipher
                appendLog("Crypto认证成功! cipher=${cipher?.algorithm}")
            }

            override fun onAuthenticationFailed() {
                appendLog("Crypto认证失败")
            }
        })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("CryptoObject 绑定认证")
            .setSubtitle("认证后可使用绑定的 Cipher 进行加密操作")
            .setNegativeButtonText("取消")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        // 简化演示: 不创建真实 CryptoObject（需要 KeyStore 配置）
        prompt.authenticate(info)
        appendLog("已启动 Crypto BiometricPrompt ...")
    }

    private fun appendLog(msg: String) {
        val current = binding.tvAuthLog.text?.toString().orEmpty()
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 1000
        binding.tvAuthLog.text = "[$timestamp] $msg\n$current"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
