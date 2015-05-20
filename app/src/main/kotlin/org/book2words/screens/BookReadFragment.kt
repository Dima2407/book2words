package org.book2words.screens

import android.app.Fragment
import android.app.ListFragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.easydictionary.app.R
import org.book2words.dao.LibraryBook
import org.book2words.services.BookReadService

public class BookReadFragment : ListFragment() {

    private var reader: BookReadService.BookReaderBinder ? = null

    private val connection = object : ServiceConnection {

        override public fun onServiceConnected(className: ComponentName, service: IBinder) {
            reader = service as BookReadService.BookReaderBinder
            bound = true;
            read()
        }

        override public fun onServiceDisconnected(arg0: ComponentName) {
            bound = false;
        }
    }

    private var bound = false
    private var book: LibraryBook? = null;

    override fun onStart() {
        super.onStart()
        val intent = Intent(getActivity(), javaClass<BookReadService>())
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        book = getArguments().getParcelable(EXTRA_BOOK)
    }

    private fun read() {
        reader!!.take(book as LibraryBook, { lines, words ->
            setListAdapter(ParagraphAdapter(getActivity(), lines))
        })
    }

    companion object {
        private val EXTRA_BOOK = "book"

        public fun create(book: LibraryBook): Fragment {
            val args = Bundle()
            args.putParcelable(EXTRA_BOOK, book)

            val fragment = BookReadFragment()
            fragment.setArguments(args)
            return fragment
        }
    }

    private class ParagraphAdapter(context: Context, items: List<String>) : ArrayAdapter<String>(context, -1, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.list_item_paragraph, null);
            }
            val titleView = view!!.findViewById(R.id.text_text) as TextView
            val item = getItem(position)

            titleView.setText(Html.fromHtml(item))
            return view
        }
    }
}