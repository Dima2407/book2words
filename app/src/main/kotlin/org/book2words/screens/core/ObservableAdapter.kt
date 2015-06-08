package org.book2words.screens.core

import android.support.v7.widget.RecyclerView
import java.util.ArrayList

public abstract class ObservableAdapter<T, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

    private val items: MutableList<T> = ArrayList()

    private var itemClick: ((item: T, position: Int) -> Unit)? = null

    public open fun onLoadFinished(data: List<T>?) {
        items.clear()
        items.addAll(data!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size()
    }

    public fun getItem(position: Int): T {
        return items.get(position)
    }

    public open fun onLoaderReset() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = getItem(position)
        if (itemClick != null) {
            holder.itemView.setOnClickListener({
                if (itemClick != null) {
                    itemClick!!(item, position)
                }
            })
        }
        onBindViewHolder(holder, item, position)
    }

    public abstract fun onBindViewHolder(holder: V, item : T, position: Int)

    fun setItemClickListener(itemClick: ((item: T, position: Int) -> Unit)?) {
        this.itemClick = itemClick
    }
}