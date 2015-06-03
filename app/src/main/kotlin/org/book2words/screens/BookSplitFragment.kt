package org.book2words.screens

import android.app.Fragment
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import org.book2words.R
import org.book2words.dao.LibraryBook
import org.book2words.services.BookAdapterBinder
import org.book2words.services.BookReadService


public class BookSplitFragment : Fragment() {

    private var surface: WebView? = null

    private var titleView: TextView? = null

    private var progressView: ProgressBar? = null

    private var reader: BookAdapterBinder? = null

    private var book: LibraryBook? = null;

    private var bound = false

    private val connection = object : ServiceConnection {

        override public fun onServiceConnected(className: ComponentName, service: IBinder) {
            reader = service as BookAdapterBinder
            bound = true;
            split()
        }

        override public fun onServiceDisconnected(arg0: ComponentName) {
            bound = false;
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_split, null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view!!.findViewById(R.id.text_title) as TextView
        progressView = view.findViewById(R.id.progress_split) as ProgressBar
        surface = view.findViewById(android.R.id.text1) as WebView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        book : LibraryBook = getArguments()!!.getParcelable(BOOK_PATH_KEY);
    }

    override fun onStart() {
        super.onStart()
        BookReadService.bindForAdapt(getActivity(), connection, book!!)
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }
    }

    private fun split() {
        reader!!.prepare(surface!!, onPrepared = {
            title, size ->
            titleView!!.setText(title)
            progressView!!.setIndeterminate(false)
            progressView!!.setMax(size)
            progressView!!.setProgress(0)
        }, onReleased = {
            getActivity().finish()
        })
        reader!!.start(onProgress = {
            i, max ->
            progressView!!.setProgress(i)
        })
    }

    companion object {

        private val BOOK_PATH_KEY = "book"

        public fun create(root: LibraryBook): Fragment {
            val args = Bundle()
            args.putParcelable(BOOK_PATH_KEY, root)

            val fragment = BookSplitFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}