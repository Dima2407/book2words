package org.book2words.screens

import android.app.Activity
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import org.book2words.R
import org.book2words.SelectFileActivity
import org.book2words.SplitActivity
import org.book2words.activities.ReaderActivity
import org.book2words.core.FileStorage
import org.book2words.dao.LibraryBook
import org.book2words.dao.LibraryDictionary
import org.book2words.data.DataContext
import org.book2words.screens.core.ObservableAdapter
import org.book2words.screens.core.ObservableListFragment
import org.book2words.screens.loaders.BaseObserver
import org.book2words.screens.loaders.ObservableLoader
import org.book2words.services.LibraryService
import java.io.File

public class LibraryListFragment : ObservableListFragment<LibraryBook>() {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<LibraryBook>>? {
        return BooksLoader(getActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = LibraryFileAdapter(getActivity())
        setListAdapter(adapter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_books, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view!!.findViewById(R.id.button_add).setOnClickListener({
            val intent = Intent(getActivity(), javaClass<SelectFileActivity>())
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, "epub")
            startActivityForResult(intent, REQUEST_CODE_BOOK)
        })
    }

    override fun onItemClick(item: LibraryBook, position: Int, id: Long) {
        if (item.getAdapted() == LibraryBook.ADAPTED) {
            openReadActivity(item)
        } else if (item.getAdapted() == LibraryBook.NONE) {
            val dictionary = LibraryDictionary(item.getDictionaryName(), item.getLanguage())
            DataContext.getLibraryDictionaryDao(getActivity()).insertOrIgnore(dictionary)
            getActivity().sendBroadcast(Intent(LibraryDictionary.ACTION_CREATED))
            openSplitActivity(item)
        }
    }

    private fun openReadActivity(book: LibraryBook) {
        val intent = Intent(getActivity(), javaClass<ReaderActivity>())
        intent.putExtra(ReaderActivity.EXTRA_BOOK, book)
        startActivityForResult(intent, 0)
    }

    private fun openSplitActivity(book: LibraryBook) {
        val intent = Intent(getActivity(), javaClass<SplitActivity>())
        intent.putExtra(SplitActivity.EXTRA_BOOK, book)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == R.id.action_add) {
            val intent = Intent(getActivity(), javaClass<SelectFileActivity>())
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, "epub")
            startActivityForResult(intent, REQUEST_CODE_BOOK)
            return true;
        }
        if (item?.getItemId() == R.id.action_import) {
            val intent = Intent(getActivity(), javaClass<SelectFileActivity>())
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, "zip")
            startActivityForResult(intent, REQUEST_CODE_IMPORT)
            return true;
        }
        if (item?.getItemId() == R.id.action_export) {
            LibraryService.export(getActivity())
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (REQUEST_CODE_BOOK == requestCode) {
                val path = data!!.getStringExtra(SelectFileActivity.EXTRA_OUTPUT)
                LibraryService.addBook(getActivity(), File(path))
            }
            if (REQUEST_CODE_IMPORT == requestCode) {
                val path = data!!.getStringExtra(SelectFileActivity.EXTRA_OUTPUT)
                LibraryService.import(getActivity(), File(path))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        private val REQUEST_CODE_IMPORT = 10
        private val REQUEST_CODE_BOOK = 20

        public fun create(): Fragment {
            val fragment = LibraryListFragment()
            return fragment
        }
    }

    private class LibraryFileAdapter(private val context: Context) :
            ObservableAdapter<LibraryBook, BookViewHolder>() {
        override fun onBindViewHolder(holder: BookViewHolder, item: LibraryBook, position: Int) {
            if (item.getAdapted() == LibraryBook.ADAPTED) {
                holder.wordsView.setVisibility(View.VISIBLE)
                holder.wordsView.setText("${item.getUnknownWordsCount()} / ${item.getWordsCount()}")
            } else {
                holder.wordsView.setVisibility(View.GONE)
            }
            val coverUri = FileStorage.imageCoverUri(item.getId())

            ImageLoader.getInstance().displayImage(coverUri, holder.coverView)
            holder.titleView.setText(item.getName())
            holder.authorsView.setText(item.getAuthors())
            holder.languageView.setText(item.getLanguage())
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_book, parent, false);
            val vh = BookViewHolder(view);
            return vh;
        }
    }

    private class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val authorsView: TextView
        val languageView: TextView
        val wordsView: TextView
        val coverView: ImageView

        init {
            titleView = view.findViewById(R.id.text_title) as TextView
            wordsView = view.findViewById(R.id.text_words) as TextView
            languageView = view.findViewById(R.id.text_language) as TextView
            authorsView = view.findViewById(R.id.text_author) as TextView
            coverView = view.findViewById(R.id.image_cover) as ImageView
        }
    }

    private class BooksLoader(private val context: Activity) : ObservableLoader<LibraryBook>(context) {
        override fun createObserver(): BroadcastReceiver {
            return BaseObserver(this, LibraryService.ACTION_PREPARED,
                    LibraryService.ACTION_CLEARED)
        }

        override fun loadInBackground(): List<LibraryBook> {
            return DataContext.getLibraryBookDao(context).loadAll()
        }
    }
}