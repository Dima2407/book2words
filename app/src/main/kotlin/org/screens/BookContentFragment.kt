package org.screens

import android.app.Fragment
import android.app.ListFragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.easydictionary.app.MarkActivity
import com.easydictionary.app.R
import com.easydictionary.app.ReaderActivity
import com.easydictionary.app.TranslateService
import org.book2dictionary.core.reader.BookReader


public class BookContentFragment : Fragment() {
    private var coverView: ImageView? = null
    private var titleView: TextView? = null

    private var path : Uri? = null;
    private var reader : BookReader? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_content, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coverView = view!!.findViewById(android.R.id.icon1) as ImageView?
        titleView = view!!.findViewById(android.R.id.text1) as TextView?

        view!!.findViewById(R.id.text_split).setOnClickListener {
            openReaderActivity()
        }
        view!!.findViewById(R.id.text_dictionary).setOnClickListener {
            openMarkActivity()
        }
    }

    private fun openReaderActivity() {
        val intent = Intent(getActivity(), javaClass<ReaderActivity>())
        intent.setData(path)
        startActivity(intent)
    }

    private fun openMarkActivity() {
        val intent = Intent(getActivity(), javaClass<MarkActivity>())
        intent.putExtra(MarkActivity.EXTRA_NAME, reader!!.getTitle())
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        path = getArguments()!!.getParcelable(BOOK_PATH_KEY);
        val bookReader = BookReader.create(path!!.getPath())
        bookReader.open()

        coverView!!.setImageBitmap(bookReader.getCover())
        titleView!!.setText(bookReader.getDisplayTitle())

        bookReader.close()
    }

    companion object {

        private val BOOK_PATH_KEY = "book"

        public fun create(root: Uri): Fragment {
            val args = Bundle()
            args.putParcelable(BOOK_PATH_KEY, root)

            val fragment = BookContentFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}