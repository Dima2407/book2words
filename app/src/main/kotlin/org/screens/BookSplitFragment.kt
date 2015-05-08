package org.screens

import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import com.easydictionary.app.R
import nl.siegmann.epublib.domain.Book
import org.book2words.dao.LibraryBook
import org.models.split.BookSplitter
import java.nio.charset.Charset


public class BookSplitFragment : Fragment() {

    private var surface: WebView? = null

    private var titleView :TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_split, null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view!!.findViewById(android.R.id.text2) as TextView
        surface = view!!.findViewById(android.R.id.text1) as WebView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val book : LibraryBook = getArguments()!!.getParcelable(BOOK_PATH_KEY);
        val splitter = BookSplitter(book, surface as WebView);

        titleView!!.setText(splitter.title)
        splitter.split()
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