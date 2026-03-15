package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentParcelableBinding

/**
 * Parcelable/Parcel 序列化示例
 * 
 * Parcelable: Android 高效序列化接口
 * - 比 Serializable 更高效
 * - 用于 Intent、Bundle、AIPC 数据传递
 * 
 * Parcel: 数据容器
 * - writeParcelable(): 写入 Parcelable 对象
 * - readParcelable(): 读取 Parcelable 对象
 * - marshall(): 序列化为字节数组
 * - unmarshall(): 反序列化
 */
class ParcelableFragment : Fragment() {

    private var _binding: FragmentParcelableBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParcelableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        showParcelableInfo()
    }

    private fun setupViews() {
        binding.btnCreateParcelable.setOnClickListener {
            createAndSerialize()
        }

        binding.btnBundlePass.setOnClickListener {
            demonstrateBundlePass()
        }

        binding.btnParcelBytes.setOnClickListener {
            demonstrateParcelBytes()
        }

        binding.btnCompareSerializable.setOnClickListener {
            compareWithSerializable()
        }
    }

    private fun showParcelableInfo() {
        val sb = StringBuilder()
        sb.appendLine("=== Parcelable 接口 ===")
        sb.appendLine("实现方法:")
        sb.appendLine("1. writeToParcel(Parcel, int)")
        sb.appendLine("   - 将对象数据写入 Parcel")
        sb.appendLine("2. CREATOR: Parcelable.Creator<T>")
        sb.appendLine("   - createFromParcel(Parcel)")
        sb.appendLine("   - newArray(int)")
        sb.appendLine("3. describeContents()")
        sb.appendLine("   - 返回 0 或 CONTENTS_FILE_DESCRIPTOR")
        sb.appendLine()
        sb.appendLine("=== Parcel 常用方法 ===")
        sb.appendLine("写入: writeInt, writeLong, writeString...")
        sb.appendLine("读取: readInt, readLong, readString...")
        sb.appendLine("序列化: marshall() -> ByteArray")
        sb.appendLine("反序列化: unmarshall(bytes, 0, len)")
        
        binding.tvInfo.text = sb.toString()
    }

    private fun createAndSerialize() {
        val sb = StringBuilder()
        sb.appendLine("=== 创建 Parcelable 对象 ===")
        
        // 创建用户对象
        val user = User(
            id = 1001,
            name = "张三",
            age = 25,
            email = "zhangsan@example.com",
            scores = listOf(95, 88, 92)
        )
        sb.appendLine("原始对象: $user")
        sb.appendLine()
        
        // 使用 Parcel 序列化
        val parcel = Parcel.obtain()
        try {
            user.writeToParcel(parcel, 0)
            
            // 获取数据大小
            val dataSize = parcel.dataSize()
            sb.appendLine("Parcel 数据大小: $dataSize bytes")
            sb.appendLine()
            
            // 重置 Parcel 位置并读取
            parcel.setDataPosition(0)
            val restoredUser = User.CREATOR.createFromParcel(parcel)
            sb.appendLine("反序列化后: $restoredUser")
            sb.appendLine()
            
            // 验证数据一致性
            sb.appendLine("数据一致性: ${user == restoredUser}")
            
        } finally {
            parcel.recycle()
        }
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateBundlePass() {
        val sb = StringBuilder()
        sb.appendLine("=== Bundle 传递 Parcelable ===")
        
        // 创建对象并放入 Bundle
        val user = User(
            id = 2001,
            name = "李四",
            age = 30,
            email = "lisi@example.com",
            scores = listOf(80, 85, 90)
        )
        
        val bundle = Bundle().apply {
            putParcelable("user", user)
            putString("extra", "额外数据")
        }
        
        sb.appendLine("放入 Bundle:")
        sb.appendLine("  putParcelable(\"user\", user)")
        sb.appendLine()
        
        // 从 Bundle 读取
        val restoredUser = bundle.getParcelable<User>("user")
        val extra = bundle.getString("extra")
        
        sb.appendLine("从 Bundle 读取:")
        sb.appendLine("  用户: $restoredUser")
        sb.appendLine("  额外数据: $extra")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateParcelBytes() {
        val sb = StringBuilder()
        sb.appendLine("=== Parcel 字节数组 ===")
        
        val user = User(
            id = 3001,
            name = "王五",
            age = 28,
            email = "wangwu@example.com",
            scores = listOf(70, 75, 80)
        )
        
        // 序列化为字节数组
        val parcel = Parcel.obtain()
        try {
            user.writeToParcel(parcel, 0)
            val bytes = parcel.marshall()
            
            sb.appendLine("序列化为字节数组:")
            sb.appendLine("  长度: ${bytes.size} bytes")
            sb.appendLine("  前16字节: ${bytes.take(16).joinToString(" ") { "%02X".format(it) }}")
            sb.appendLine()
            
            // 从字节数组反序列化
            val newParcel = Parcel.obtain()
            try {
                newParcel.unmarshall(bytes, 0, bytes.size)
                newParcel.setDataPosition(0)
                val restoredUser = User.CREATOR.createFromParcel(newParcel)
                
                sb.appendLine("从字节数组反序列化:")
                sb.appendLine("  $restoredUser")
            } finally {
                newParcel.recycle()
            }
            
        } finally {
            parcel.recycle()
        }
        
        binding.tvResult.text = sb.toString()
    }

    private fun compareWithSerializable() {
        val sb = StringBuilder()
        sb.appendLine("=== Parcelable vs Serializable ===")
        sb.appendLine()
        sb.appendLine("【Parcelable】")
        sb.appendLine("✓ 性能更好 (约10倍快)")
        sb.appendLine("✓ 内存开销小")
        sb.appendLine("✓ Android 官方推荐")
        sb.appendLine("✗ 需要样板代码")
        sb.appendLine("✗ 需要手动实现序列化逻辑")
        sb.appendLine()
        sb.appendLine("【Serializable】")
        sb.appendLine("✓ 实现简单 (仅实现接口)")
        sb.appendLine("✓ Java 标准接口")
        sb.appendLine("✗ 性能较差 (反射开销)")
        sb.appendLine("✗ 会产生临时对象")
        sb.appendLine()
        sb.appendLine("【推荐使用场景】")
        sb.appendLine("• Intent 传参: Parcelable")
        sb.appendLine("• Bundle 存储: Parcelable")
        sb.appendLine("• AIDL 通信: Parcelable")
        sb.appendLine("• 文件存储: Serializable 或 JSON")
        
        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ParcelableFragment()
    }

    /**
     * 示例 Parcelable 实体类
     */
    data class User(
        val id: Int,
        val name: String,
        val age: Int,
        val email: String,
        val scores: List<Int>
    ) : Parcelable {
        
        constructor(parcel: Parcel) : this(
            id = parcel.readInt(),
            name = parcel.readString() ?: "",
            age = parcel.readInt(),
            email = parcel.readString() ?: "",
            scores = parcel.createIntArray()?.toList() ?: emptyList()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(name)
            parcel.writeInt(age)
            parcel.writeString(email)
            parcel.writeIntArray(scores.toIntArray())
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }
}
