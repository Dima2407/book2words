package org.book2words.screens

import android.app.Fragment
import android.app.ListFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import org.book2words.R
import org.book2words.ReaderActivity
import org.book2words.SelectFolderActivity
import org.book2words.core.FileStorage
import org.book2words.dao.LibraryBook
import org.book2words.data.DataContext
import org.book2words.services.LibraryService

public class LibraryListFragment : ListFragment() {

    private var adapter: LibraryFileAdapter? = null

    private val receiver = object : BroadcastReceiver () {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getAction()
            when (action) {
                LibraryService.ACTION_PREPARED -> {
                    loadData()
                }
            }
        }
    }

    private fun loadData() {
        adapter!!.clear()
        val items = DataContext.getLibraryBookDao(this).loadAll()
        adapter!!.addAll(items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val items = DataContext.getLibraryBookDao(this).loadAll()
        adapter = LibraryFileAdapter(getActivity(), items)
        setListAdapter(adapter)
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val book = l!!.getItemAtPosition(position) as LibraryBook
        if (book.getAdapted()) {
            if(book.getRead()){
                val intent = Intent(getActivity(), javaClass<ReaderActivity>())
                intent.putExtra(ReaderActivity.EXTRA_BOOK, book)
                startActivityForResult(intent, 0)
            } else {
                val fragment = DictionaryCreateDialogFragment.create(book)
                fragment.show(getFragmentManager(), "fragment:dictionary_create")
            }
        } else {
            val fragment = DictionaryDialogListFragment.create(book)
            fragment.show(getFragmentManager(), "fragment:dictionary_list")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.getItemId() == R.id.action_add) {
            startActivityForResult(Intent(getActivity(), javaClass<SelectFolderActivity>()), 0)
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        public fun create(): Fragment {
            val fragment = LibraryListFragment()
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(LibraryService.ACTION_PREPARED)
        intentFilter.addAction(LibraryService.ACTION_CLEARED)
        getActivity().registerReceiver(receiver,
                intentFilter)
    }

    override fun onPause() {
        super.onPause()
        getActivity().unregisterReceiver(receiver)
    }

    private class LibraryFileAdapter(context: Context, objects: List<LibraryBook>)
    : ArrayAdapter<LibraryBook>(context, -1, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.list_item_book, null);
            }
            val titleView = view!!.findViewById(R.id.text_title) as TextView
            val authorsView = view!!.findViewById(R.id.text_author) as TextView
            val wordsView = view!!.findViewById(R.id.text_words) as TextView

            val coverView = view!!.findViewById(R.id.image_cover) as ImageView

            val item = getItem(position);
            if(item.getAdapted()){
                wordsView.setVisibility(View.VISIBLE)
                wordsView.setText("${item.getUnknownWords()} / ${item.getAllWords()}")
            }else{
                wordsView.setVisibility(View.GONE)
            }
            val coverUri = FileStorage.imageCoverUri(item.getId())

            ImageLoader.getInstance().displayImage(coverUri, coverView);
            titleView.setText(item.getName())
            authorsView.setText(item.getAuthors())
            return view
        }
    }
}