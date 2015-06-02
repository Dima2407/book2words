package org.book2words.screens.core

import android.app.Fragment
import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle

public abstract class ObservableListFragment<T> : Fragment(), LoaderManager.LoaderCallbacks<List<T>> {

    private var adapter: ObservableAdapter<T>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    override fun onLoaderReset(loader: Loader<List<T>>?) {
        adapter?.onLoaderReset();
    }

    override fun onLoadFinished(loader: Loader<List<T>>?, data: List<T>?) {
        adapter?.onLoadFinished(data);
    }

    public fun setListAdapter(adapter: ObservableAdapter<T>) {
        this.adapter = adapter
    }
}