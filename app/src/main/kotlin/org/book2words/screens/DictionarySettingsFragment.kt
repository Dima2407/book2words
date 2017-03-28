package org.book2words.screens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import org.book2words.DictionaryActivity
import org.book2words.R
import org.book2words.core.FileStorage
import org.book2words.models.LibraryDictionary
import org.book2words.data.DataContext
import org.book2words.screens.core.ObservableAdapter
import org.book2words.screens.core.ObservableListFragment
import org.book2words.screens.loaders.BaseObserver
import org.book2words.screens.loaders.ObservableLoader

class DictionarySettingsFragment : ObservableListFragment<LibraryDictionary>() {
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<LibraryDictionary>>? {
        return DictionariesLoader(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = LibraryDictionaryAdapter(activity)
        setListAdapter(adapter)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(activity, 1)
    }

    override fun onItemClick(item: LibraryDictionary, position: Int, id: Long) {
        val intent = Intent(activity, DictionaryActivity::class.java)
        intent.putExtra(DictionaryActivity.EXTRA_DICTIONARY, item)
        startActivity(intent)
    }

    private class LibraryDictionaryAdapter(
            private val context: Activity) :
            ObservableAdapter<LibraryDictionary, DictionaryViewHolder>() {
        override fun onBindViewHolder(holder: DictionaryViewHolder, item: LibraryDictionary, position: Int) {
            holder.titleView.text = item.name
            holder.countView.text = "${item.size}"
        }

        override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): DictionaryViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_dictionary, p0, false)
            val vh = DictionaryViewHolder(view)
            return vh
        }
    }

    private class DictionaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val countView: TextView

        init {
            titleView = view.findViewById(R.id.text_title) as TextView
            countView = view.findViewById(R.id.text_words) as TextView
        }
    }

    private class DictionariesLoader(private val context: Activity) : ObservableLoader<LibraryDictionary>(context) {
        override fun createObserver(): BroadcastReceiver {
            return BaseObserver(this,
                    LibraryDictionary.ACTION_UPDATED,
                    LibraryDictionary.ACTION_CREATED,
                    LibraryDictionary.ACTION_DELETED)
        }

        override fun loadInBackground(): List<LibraryDictionary> {
            val dir = FileStorage.createDictionaryDirectory()
            val dictionaries = dir.listFiles().map { file ->
                val reader = file.bufferedReader()
                val size = reader.readLines().size
                reader.close()
                LibraryDictionary(file.nameWithoutExtension, size)
            }.toList();
/*
            val title = context.getString(R.string.default_dictionary)
            val size = context.resources.getInteger(R.integer.default_dictionary)
            dictionaries.add(LibraryDictionary(-1, title, true, true, "en", size))

            val stored = DataContext.getLibraryDictionaryDao(context).loadAll()
            dictionaries.addAll(stored)
*/
            return dictionaries
        }
    }
}