package org.book2words.screens

import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.book2words.R
import org.book2words.dao.LibraryBook
import org.book2words.models.book.ParagraphAdapted
import org.book2words.screens.ui.DefinitionView
import org.book2words.services.BookReadService

public class BookReadFragment : Fragment() {

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
    private var listView: RecyclerView? = null;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read, null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view!!.findViewById(android.R.id.list) as RecyclerView;
    }

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
        listView!!.setHasFixedSize(true);

        listView!!.setLayoutManager(LinearLayoutManager(getActivity()));
    }

    private fun read() {
        reader!!.take(book as LibraryBook, { lines, words ->
            listView!!.setAdapter(ParagraphAdapter(getActivity(), reader as BookReadService.BookReaderBinder, lines))
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

    private class ParagraphAdapter(private val context: Context, private val binder: BookReadService.BookReaderBinder, private val items: List<ParagraphAdapted>) : RecyclerView.Adapter<ParagraphViewHolder>() {
        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onBindViewHolder(p0: ParagraphViewHolder?, p1: Int) {
            val item = items[p1]
            p0!!.titleView.setText(item.getAdapted(), TextView.BufferType.SPANNABLE)
            p0!!.wordsView.removeAllViews()
            item.getWords().forEach {
                val word = it.word
                val rootView = View.inflate(context, R.layout.list_item_word, null) as ViewGroup
                rootView.findViewById(R.id.button_save).setOnClickListener({
                    binder.addToDictionary(word)
                })
                val textView = rootView.findViewById(R.id.progress_loading)
                textView.setVisibility(View.VISIBLE)
                p0!!.wordsView.addView(rootView)
                binder.translate(word, {
                    input, results ->
                    textView.setVisibility(View.GONE)
                    results!!.results()!!.forEach {

                        val definitionView = DefinitionView(context)
                        definitionView.setDefinition(it)
                        rootView.addView(definitionView)
                    }
                })

                rootView.setBackgroundColor(it.color)
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ParagraphViewHolder? {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_paragraph, p0, false);
            val vh = ParagraphViewHolder(view);
            return vh;
        }
    }

    private class ParagraphViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val wordsView: LinearLayout

        init {
            titleView = view.findViewById(R.id.text_text) as TextView
            wordsView = view.findViewById(R.id.text_words) as LinearLayout
        }
    }
}