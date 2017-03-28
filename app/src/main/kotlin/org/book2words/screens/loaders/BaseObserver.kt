package org.book2words.screens.loaders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.AsyncTaskLoader

public class BaseObserver<T>(private val mLoader: AsyncTaskLoader<T>, vararg filters: String) : BroadcastReceiver() {

    init {
        val filter = IntentFilter()
        for (action in filters) {
            filter.addAction(action)
        }
        mLoader.getContext().registerReceiver(this, filter)
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (isChanged(intent)) {
            mLoader.onContentChanged()
        }
    }

    protected fun isChanged(intent: Intent): Boolean {
        return true
    }
}
