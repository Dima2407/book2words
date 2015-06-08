package org.book2words.screens.core

import android.app.Fragment
import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle
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
        getLoaderManager().initLoader(0, getArguments(), this)
        progressView!!.setVisibility(View.VISIBLE)
        listView!!.setHasFixedSize(true);

        listView!!.setLayoutManager(LinearLayoutManager(getActivity()))
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        listView = view!!.findViewById(android.R.id.list) as RecyclerView
        progressView = view!!.findViewById(R.id.progress_loading)
    }

    override fun onLoaderReset(loader: Loader<List<T>>?) {
        progressView!!.setVisibility(View.GONE)
        listView!!.setVisibility(View.VISIBLE)
        adapter?.onLoaderReset();
    }

    override fun onLoadFinished(loader: Loader<List<T>>?, data: List<T>?) {
        progressView!!.setVisibility(View.GONE)
        listView!!.setVisibility(View.VISIBLE)
        adapter?.onLoadFinished(data);
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