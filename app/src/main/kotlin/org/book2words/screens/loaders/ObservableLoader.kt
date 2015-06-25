package org.book2words.screens.loaders

import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.content.AsyncTaskLoader

public abstract class ObservableLoader<T>(context: Context, private val onDataChanged: (() -> Unit)? = null) : AsyncTaskLoader<List<T>>(context) {

    private var mItems: List<T>? = null

    private val mLastConfig = InterestingConfigChanges()

    private var mObserver: BroadcastReceiver? = null

    override fun deliverResult(pList: List<T>?) {

        if (isReset()) {
            if (pList != null) {
                onReleaseResources(pList)
            }
        }
        val oldItems = pList
        mItems = pList

        if (isStarted()) {

            super.deliverResult(pList)
        }

        if (oldItems != null) {
            onReleaseResources(oldItems)
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    override fun onStartLoading() {

        if (mItems != null) {

            deliverResult(mItems)
        }

        if (mObserver == null) {
            mObserver = createObserver()
        }

        val configChange = mLastConfig.applyNewConfig(getContext().getResources())

        if (takeContentChanged() || mItems == null || configChange) {
            forceLoad()
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    override fun onStopLoading() {

        cancelLoad()
    }

    /**
     * Handles a request to cancel a load.
     */
    override fun onCanceled(apps: List<T>?) {

        super.onCanceled(apps)

        onReleaseResources(apps)
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    override fun onReset() {

        super.onReset()

        onStopLoading()

        if (mItems != null) {
            onReleaseResources(mItems)
            mItems = null
        }

        if (mObserver != null) {
            getContext().unregisterReceiver(mObserver)
            mObserver = null
        }
    }

    protected fun onReleaseResources(pList: List<T>?) {
    }

    override fun onContentChanged() {
        if (onDataChanged != null) {
            onDataChanged!!()
        }
        super.onContentChanged()
    }

    protected abstract fun createObserver(): BroadcastReceiver
}