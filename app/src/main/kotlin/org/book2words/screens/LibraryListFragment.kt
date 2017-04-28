package org.book2words.screens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import org.book2words.R
import org.book2words.SelectFileActivity
import org.book2words.SplitActivity
import org.book2words.activities.ReaderActivity
import org.book2words.core.Logger
import org.book2words.data.DataContext
import org.book2words.database.models.LibraryBook
import org.book2words.screens.core.ObservableAdapter
import org.book2words.screens.core.ObservableListFragment
import org.book2words.screens.loaders.BaseObserver
import org.book2words.screens.loaders.ObservableLoader
import org.book2words.services.LibraryService
import java.io.File


class LibraryListFragment : ObservableListFragment<LibraryListFragment.LibraryBookView>() {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<LibraryBookView>>? {
        return BooksLoader(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val padding = 2 * (1 * resources.displayMetrics.density).toInt()//both sides
        val contentMargin = 2 * (6 * resources.displayMetrics.density).toInt()
        val topBarHeight = (24 * resources.displayMetrics.density).toInt()
        val adapter = LibraryFileAdapter(activity,
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels - topBarHeight - contentMargin
                ,
                context.resources.getDimensionPixelSize(R.dimen.book_width)
                + padding,
                context.resources.getDimensionPixelSize(R.dimen.book_height)
                        + padding)
        setListAdapter(adapter)

        addItemDecoration(GridSpacingItemDecoration(adapter.columnsCount, adapter.spaceWidth, true))
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        val dpWidth = context.resources.displayMetrics.widthPixels
        val spanCount = dpWidth / context.resources.getDimensionPixelSize(R.dimen.book_width)
        Logger.debug("createLayoutManager() $spanCount")
        val layoutManager = GridLayoutManager(activity, spanCount)
        return layoutManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_books, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemClick(item: LibraryBookView, position: Int, id: Long) {
        if (item.book == null) {
            val intent = Intent(activity, SelectFileActivity::class.java)
            intent.putExtra(SelectFileActivity.EXTRA_EXTENSION, arrayOf("epub", "txt"))
            startActivityForResult(intent, REQUEST_CODE_BOOK)
            return
        }
        if (item.book!!.adapted == LibraryBook.ADAPTED) {
            openReadActivity(item.book!!)
        } else if (item.book!!.adapted == LibraryBook.NONE) {
            openSplitActivity(item.book!!)
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

        val TAG = LibraryListFragment::class.simpleName

        private val REQUEST_CODE_IMPORT = 10
        private val REQUEST_CODE_BOOK = 20

        fun create(): Fragment {
            val fragment = LibraryListFragment()
            return fragment
        }
    }

    private class LibraryFileAdapter(private val context: Context,
                                     private val containerWidth: Int,
                                     private val containerHeight: Int,
                                     private val itemWidth: Int,
                                     private val itemHeight: Int) :
            ObservableAdapter<LibraryBookView, BookViewHolder>() {
        override fun onBindViewHolder(holder: BookViewHolder, item: LibraryBookView, position: Int) {
            if (item.book == null) {
                holder.wordsView.text = "Add Book"
                holder.coverView.setImageDrawable(ColorDrawable(Color.RED));
            } else {
                if (item.book!!.adapted == LibraryBook.ADAPTED) {
                    holder.coverView.setImageDrawable(ColorDrawable(Color.GREEN));
                } else {
                    holder.coverView.setImageDrawable(ColorDrawable(Color.BLUE));
                }
                holder.wordsView.text = File(item.book!!.path).name
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_book, parent, false)
            val vh = BookViewHolder(view)
            return vh
        }

        val columnsCount: Int
            get () = containerWidth / itemWidth

        val rowsCount: Int
            get () = containerHeight / itemHeight

        val spaceWidth: Int
            get() = (containerWidth - (columnsCount * itemWidth)) / columnsCount


        override fun onLoadFinished(data: List<LibraryBookView>?) {
            var visibleRows = rowsCount
            while (visibleRows * (itemHeight + spaceWidth) + spaceWidth > containerHeight){
                visibleRows--
            }
            var items = visibleRows * columnsCount
            (data?.size).let {
                if(items < it!!){
                    items = it
                }
            }
            val result = MutableList(items, { index ->
                LibraryBookView(data?.getOrNull(index)?.book)
            })
            super.onLoadFinished(result)
        }
    }

    private class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordsView: TextView = view.findViewById(R.id.text_title) as TextView
        val coverView: ImageView = view.findViewById(R.id.image_cover) as ImageView

    }

    private class BooksLoader(private val context: Activity) : ObservableLoader<LibraryBookView>(context) {
        override fun createObserver(): BroadcastReceiver {
            return BaseObserver(this, LibraryService.ACTION_PREPARED,
                    LibraryService.ACTION_CLEARED)
        }

        override fun loadInBackground(): List<LibraryBookView> {
            return DataContext.getLibraryBookDao(context).allBooks
                    .map { LibraryBookView(it) }
        }
    }

    class LibraryBookView(var book: LibraryBook?) {
        var empty = book == null
    }


    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}