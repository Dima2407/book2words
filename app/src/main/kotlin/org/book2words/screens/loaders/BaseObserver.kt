package org.book2words.screens.loaders

import android.content.*

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
