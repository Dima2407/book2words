package org.book2words.screens.core

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.book2words.R

public abstract class ObservableListFragment<T> : Fragment(), LoaderManager.LoaderCallbacks<List<T>> {

    private var adapter: ObservableAdapter<T, out  RecyclerView.ViewHolder>? = null
    private var listView: RecyclerView? = null
    private var progressView : View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        loaderManager.initLoader(0, arguments, this)
        progressView!!.visibility = View.VISIBLE
        listView!!.setHasFixedSize(true)
        listView!!.layoutManager = createLayoutManager()
    }

    protected open fun createLayoutManager() : RecyclerView.LayoutManager {
        return LinearLayoutManager(activity)
    }

    protected fun addItemDecoration(decoration : RecyclerView.ItemDecoration){
        listView!!.addItemDecoration(decoration)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        listView = view!!.findViewById(android.R.id.list) as RecyclerView
        progressView = view!!.findViewById(R.id.progress_loading)
    }

    override fun onLoaderReset(loader: Loader<List<T>>?) {
        progressView!!.setVisibility(View.GONE)
        listView!!.setVisibility(View.VISIBLE)
        adapter?.onLoaderReset()
    }

    override fun onLoadFinished(loader: Loader<List<T>>?, data: List<T>?) {
        progressView!!.setVisibility(View.GONE)
        listView!!.setVisibility(View.VISIBLE)
        adapter?.onLoadFinished(data)
    }

    public fun setListAdapter(adapter: ObservableAdapter<T, out RecyclerView.ViewHolder>) {
        this.adapter = adapter
        this.adapter!!.setItemClickListener({
            item, position ->
            onItemClick(item, position, position.toLong())
        })
        listView!!.setAdapter(adapter)
    }

    public open fun onItemClick(item : T , position: Int, id: Long) {

    }
}