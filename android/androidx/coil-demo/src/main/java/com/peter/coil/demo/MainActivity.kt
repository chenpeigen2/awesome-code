package com.peter.coil.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.coil.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DemoAdapter(getDemoItems()) { item ->
                item.createIntent(this@MainActivity)?.let { startActivity(it) }
            }
        }
    }

    private fun getDemoItems(): List<DemoItem> = listOf(
        DemoItem(title = "Coil 图片加载", isHeader = true),
        DemoItem(
            title = getString(R.string.basic_load),
            description = getString(R.string.basic_load_desc),
            targetClass = BasicLoadActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.transform),
            description = getString(R.string.transform_desc),
            targetClass = TransformActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.cache),
            description = getString(R.string.cache_desc),
            targetClass = CacheActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.placeholder),
            description = getString(R.string.placeholder_desc),
            targetClass = PlaceholderActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.advanced),
            description = getString(R.string.advanced_desc),
            targetClass = AdvancedActivity::class.java
        ),
        DemoItem(
            title = getString(R.string.list_load),
            description = getString(R.string.list_load_desc),
            targetClass = ListActivity::class.java
        ),
    )
}
