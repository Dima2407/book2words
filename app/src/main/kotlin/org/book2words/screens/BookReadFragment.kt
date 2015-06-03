package org.book2words.screens

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.book2words.R
import org.book2words.activities.ReaderScreen
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.WordAdapted
import org.book2words.screens.ui.WordView
import org.book2words.services.BookReaderBinder

public class BookReadFragment : Fragment() {

    private var listView: RecyclerView? = null
    private var progressView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read, null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view!!.findViewById(android.R.id.list) as RecyclerView
        progressView = view!!.findViewById(R.id.frame_progress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView!!.setHasFixedSize(true)

        listView!!.setLayoutManager(LinearLayoutManager(getActivity()))
        read()
    }

    private fun read() {
        listView!!.setVisibility(View.GONE)
        progressView!!.setVisibility(View.VISIBLE)
        val reader = (getActivity() as ReaderScreen).getReader()
        reader.read({ lines, words ->
            listView!!.setAdapter(ParagraphAdapter(getActivity(), reader, lines))
            listView!!.setVisibility(View.VISIBLE)
            progressView!!.setVisibility(View.GONE)
        })
    }

    companion object {

        public fun create(): Fragment {
            val fragment = BookReadFragment()

            return fragment
        }
    }

    private class ParagraphAdapter(private val context: Context, private val binder: BookReaderBinder, private val items: List<ParagraphAdapted>) : RecyclerView.Adapter<ParagraphViewHolder>() {
        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onBindViewHolder(holder: ParagraphViewHolder?, p1: Int) {
            val item = items[p1]
            holder!!.titleView.setText(item.getAdapted(), TextView.BufferType.SPANNABLE)
            holder.wordsView.removeAllViews()
            item.getWords().forEach {
                val rootView = WordView(context)
                rootView.setOnWordSaveListener { view, s ->
                    removeWord(s!!)
                    binder.remove(s)
                }
                rootView.setWord(it)
                if (!it.translated) {

                    binder.translate(it.word, {
                        input, results ->
                        it.setDefinitions(results!!.results())
                        rootView.showDefinitions()
                    })
                }
                holder.wordsView.addView(rootView)
            }
        }

        private fun removeWord(word: WordAdapted) {
            items.forEach {
                it.removeWord(word)
            }
            notifyDataSetChanged()
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
        val wordsView: ViewGroup

        init {
            titleView = view.findViewById(R.id.text_text) as TextView
            wordsView = view.findViewById(R.id.text_words) as ViewGroup
        }
    }
}