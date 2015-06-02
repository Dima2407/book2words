package org.book2words.screens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Loader
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import org.book2words.DictionaryActivity
import org.book2words.R
import org.book2words.dao.LibraryDictionary
import org.book2words.data.ConfigsContext
import org.book2words.data.DataContext
import org.book2words.screens.core.ObservableAdapter
import org.book2words.screens.core.ObservableListFragment
import org.book2words.screens.loaders.BaseObserver
import org.book2words.screens.loaders.ObservableLoader
import org.book2words.services.net.B2WService
import java.util.ArrayList

public class DictionarySettingsFragment : ObservableListFragment<LibraryDictionary>() {
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<LibraryDictionary>>? {
        return DictionariesLoader(getActivity())
    }

    private var listView: RecyclerView? = null

    private var paragraphsView: SeekBar? = null
    private var paragraphsSplitView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view!!.findViewById(android.R.id.list) as RecyclerView
        paragraphsView = view.findViewById(R.id.seek_paragraphs) as SeekBar
        paragraphsSplitView = view.findViewById(R.id.text_paragraphs) as TextView

        val configs = ConfigsContext.getConfigs(getActivity())
        paragraphsSplitView!!.setText("${configs.getCurrentParagraphsInStep()}")
        paragraphsView!!.setMax(configs.getMaxParagraphsInStep() / configs.getParagraphsInStep())
        paragraphsView!!.setProgress(configs.getCurrentParagraphsInStep() / configs.getParagraphsInStep())
        paragraphsView!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val paragraphs = progress * configs.getParagraphsInStep()
                paragraphsSplitView!!.setText("$paragraphs")
                configs.setCurrentParagraphsInStep(paragraphs)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        B2WService.loadDictionaries(getActivity())

        val adapter = LibraryDictionaryAdapter(getActivity())
        setListAdapter(adapter)

        listView!!.setHasFixedSize(true);

        listView!!.setLayoutManager(LinearLayoutManager(getActivity()))
        listView!!.setAdapter(adapter)
    }

    private class LibraryDictionaryAdapter(
            private val context: Activity) : RecyclerView.Adapter<DictionaryViewHolder>(),
            ObservableAdapter<LibraryDictionary> {
        override fun onLoadFinished(data: List<LibraryDictionary>?) {
            items.addAll(data!!)
            notifyDataSetChanged()
        }

        private val items: MutableList<LibraryDictionary> = ArrayList()
        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onBindViewHolder(p0: DictionaryViewHolder?, p1: Int) {
            val item = items[p1]
            p0!!.titleView.setText(item.getName())
            p0.countView.setText("${item.getSize()}")

            p0.useView.setEnabled(!item.getReadonly())
            p0.useView.setChecked(item.getUse())
            p0.useView.setOnCheckedChangeListener { compoundButton, b ->
                if (b != item.getUse()) {
                    item.setUse(b)
                    DataContext.getLibraryDictionaryDao(context).update(item)
                }
            }

            p0.itemView.setOnClickListener({
                val intent = Intent(context, javaClass<DictionaryActivity>())
                intent.putExtra(DictionaryActivity.EXTRA_DICTIONARY, item);
                context.startActivity(intent)
            })
        }

        override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): DictionaryViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_dictionary, p0, false);
            val vh = DictionaryViewHolder(view);
            return vh;
        }
    }

    private class DictionaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val countView: TextView
        val useView: Switch

        init {
            titleView = view.findViewById(R.id.text_title) as TextView
            countView = view.findViewById(R.id.text_words) as TextView
            useView = view.findViewById(R.id.button_use) as Switch
        }
    }

    private class DictionariesLoader(private val context: Activity) : ObservableLoader<LibraryDictionary>(context) {
        override fun createObserver(): BroadcastReceiver {
            return BaseObserver(this, "")
        }

        override fun loadInBackground(): List<LibraryDictionary> {
            return DataContext.getLibraryDictionaryDao(context).loadAll()
        }

    }
}