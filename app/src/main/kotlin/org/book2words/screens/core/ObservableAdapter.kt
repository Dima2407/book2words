package org.book2words.screens.core

public interface ObservableAdapter<T> {
    fun onLoaderReset() {

    }

    fun onLoadFinished(data: List<T>?)
}