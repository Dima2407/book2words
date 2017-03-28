package org.book2words.screens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import org.book2words.R
import org.book2words.SelectFileActivity
import org.book2words.SplitActivity
import org.book2words.activities.ReaderActivity
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.data.DataContext
import org.book2words.screens.core.ObservableAdapter
import org.book2words.screens.core.ObservableListFragment
import org.book2words.screens.loaders.BaseObserver
import org.book2words.screens.loaders.ObservableLoader
import org.book2words.services.LibraryService
import java.io.File

class LibraryListFragment : ObservableListFragment<LibraryBook>() {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<LibraryBook>>? {
        return BooksLoader(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val spanCount = resources.getInteger(R.integer.span_count)
        addItemDecoration(GridSpacingItemDecoration(spanCount, 10, true))
        val adapter = LibraryFileAdapter(activity)
        setListAdapter(adapter)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        val spanCount = resources.getInteger(R.integer.span_count)
        Logger.debug("createLayoutManager() ${spanCount}")
        val layoutManager = GridLayoutManager(activity, spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val line = position / spanCount
                if (line % 2 != 0) {
                    return 1
                } else {
                    return 2
                }
            }

        }
        return layoutManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_books, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view!!.findViewById(R.id.button_add).setOnClickListener({
            val intent = Intent(activity, SelectFileActivity::class.java)
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, arrayOf("epub", "txt"))
            startActivityForResult(intent, REQUEST_CODE_BOOK)
        })
    }

    override fun onItemClick(item: LibraryBook, position: Int, id: Long) {
        if (item.adapted == LibraryBook.ADAPTED) {
            openReadActivity(item)
        } else if (item.adapted == LibraryBook.NONE) {
            openSplitActivity(item)
        }
    }

    private fun openReadActivity(book: LibraryBook) {
        val intent = Intent(activity, ReaderActivity::class.java)
        intent.putExtra(ReaderActivity.EXTRA_BOOK, book)
        startActivityForResult(intent, 0)
    }

    private fun openSplitActivity(book: LibraryBook) {
        val intent = Intent(activity, SplitActivity::class.java)
        intent.putExtra(SplitActivity.EXTRA_BOOK, book)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_sync, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_import) {
            val intent = Intent(activity, SelectFileActivity::class.java)
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, "zip")
            startActivityForResult(intent, REQUEST_CODE_IMPORT)
            return true
        }
        if (item?.itemId == R.id.action_export) {
            LibraryService.export(activity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (REQUEST_CODE_BOOK == requestCode) {
                val path = data!!.getStringExtra(SelectFileActivity.EXTRA_OUTPUT)
                LibraryService.addBook(activity, File(path))
            }
            if (REQUEST_CODE_IMPORT == requestCode) {
                val path = data!!.getStringExtra(SelectFileActivity.EXTRA_OUTPUT)
                LibraryService.import(activity, File(path))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        private val REQUEST_CODE_IMPORT = 10
        private val REQUEST_CODE_BOOK = 20

        fun create(): Fragment {
            val fragment = LibraryListFragment()
            return fragment
        }
    }

    private class LibraryFileAdapter(private val context: Context) :
            ObservableAdapter<LibraryBook, BookViewHolder>() {
        override fun onBindViewHolder(holder: BookViewHolder, item: LibraryBook, position: Int) {
            holder.wordsView.text = File(item.path).name
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_book, parent, false)
            val vh = BookViewHolder(view)
            return vh
        }
    }

    private class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordsView: TextView

        init {
            wordsView = view.findViewById(R.id.text_title) as TextView
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

    private class GridSpacingItemDecoration(private val spanCount: Int,
                                            private val spacing: Int,
                                            private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            val position = parent!!.getChildAdapterPosition(view)
            val column = position % spanCount
            if (includeEdge) {
                outRect!!.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) {
                    // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect!!.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}