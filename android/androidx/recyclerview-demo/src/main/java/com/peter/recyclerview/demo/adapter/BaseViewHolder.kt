package com.peter.recyclerview.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 基础 ViewHolder，提供便捷的视图访问
 */
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    constructor(
        parent: ViewGroup,
        @LayoutRes layoutRes: Int
    ) : this(
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    )
    
    abstract fun bind(item: T)
    
    /**
     * 获取点击事件的点击位置，考虑了 header 的情况
     */
    fun getAdapterPositionWithHeader(headerCount: Int = 0): Int {
        return bindingAdapterPosition - headerCount
    }
}

/**
 * 点击事件接口
 */
interface OnItemClickListener<T> {
    fun onItemClick(item: T, position: Int)
}

/**
 * 长按事件接口
 */
interface OnItemLongClickListener<T> {
    fun onItemLongClick(item: T, position: Int): Boolean
}

/**
 * 子视图点击事件接口
 */
interface OnChildClickListener<T> {
    fun onChildClick(view: View, item: T, position: Int)
}
