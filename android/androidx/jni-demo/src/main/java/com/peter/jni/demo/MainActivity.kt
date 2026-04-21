package com.peter.jni.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.jni.demo.advanced.DynamicRegisterActivity
import com.peter.jni.demo.advanced.ThreadCallbackActivity
import com.peter.jni.demo.basic.ArrayOperationActivity
import com.peter.jni.demo.basic.BasicCallActivity
import com.peter.jni.demo.basic.ParamPassingActivity
import com.peter.jni.demo.databinding.ActivityMainBinding
import com.peter.jni.demo.databinding.ItemDemoBinding
import com.peter.jni.demo.intermediate.CallbackActivity
import com.peter.jni.demo.intermediate.FileIOActivity
import com.peter.jni.demo.intermediate.ObjectManipulationActivity
import com.peter.jni.demo.rust.RustJniActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.app_name)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DemoAdapter(getDemoItems())
        }
    }

    private fun getDemoItems(): List<DemoItem> = listOf(
        DemoItem(
            title = getString(R.string.basic_call),
            description = getString(R.string.basic_call_desc),
            level = "L1 基础",
            targetClass = BasicCallActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.param_passing),
            description = getString(R.string.param_passing_desc),
            level = "L2 基础",
            targetClass = ParamPassingActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.array_operation),
            description = getString(R.string.array_operation_desc),
            level = "L3 基础",
            targetClass = ArrayOperationActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.object_manipulation),
            description = getString(R.string.object_manipulation_desc),
            level = "L4 进阶",
            targetClass = ObjectManipulationActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.callback),
            description = getString(R.string.callback_desc),
            level = "L5 进阶",
            targetClass = CallbackActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.file_io),
            description = getString(R.string.file_io_desc),
            level = "L6 进阶",
            targetClass = FileIOActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.dynamic_register),
            description = getString(R.string.dynamic_register_desc),
            level = "L7 高级",
            targetClass = DynamicRegisterActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.thread_callback),
            description = getString(R.string.thread_callback_desc),
            level = "L8 高级",
            targetClass = ThreadCallbackActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.rust_jni),
            description = getString(R.string.rust_jni_desc),
            level = "L9 Rust",
            targetClass = RustJniActivity::class.java
        ),
    )
}

data class DemoItem(
    val title: String,
    val description: String,
    val level: String,
    val targetClass: Class<*>
)

class DemoAdapter(
    private val items: List<DemoItem>
) : androidx.recyclerview.widget.RecyclerView.Adapter<DemoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDemoBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDescription.text = item.description
        holder.binding.tvLevel.text = item.level
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, item.targetClass))
        }
    }

    override fun getItemCount() = items.size
}
