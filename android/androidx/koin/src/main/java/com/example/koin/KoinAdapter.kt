package com.example.koin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.koin.databinding.ItemKoinBinding

class KoinAdapter(
    private val items: List<KoinItem>
) : RecyclerView.Adapter<KoinAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemKoinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: KoinItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvCodeSnippet.text = item.codeSnippet
            binding.btnDemo.setOnClickListener { item.action() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKoinBinding.inflate(
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
}
