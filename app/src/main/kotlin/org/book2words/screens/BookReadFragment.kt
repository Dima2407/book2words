package org.book2words.screens

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.graphics.Point
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
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
    private var progressView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view?.findViewById(android.R.id.list) as RecyclerView
        progressView = view?.findViewById(R.id.frame_progress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView!!.setHasFixedSize(true)

        listView!!.layoutManager = LinearLayoutManager(activity)
        read()
    }

    private fun read() {
        listView!!.visibility = View.GONE
        progressView!!.visibility = View.VISIBLE
        val reader = (activity as ReaderScreen).getReader()
        reader.read({ paragraphs ->
            listView!!.adapter = ParagraphAdapter(activity, reader, paragraphs)
            listView!!.visibility = View.VISIBLE
            progressView!!.visibility = View.GONE
        })
    }

    companion object {

        public fun create(): Fragment {
            val fragment = BookReadFragment()

            return fragment
        }
    }

    private class ParagraphAdapter(
            private val context: Activity,
            private val binder: BookReaderBinder,
            private val items: List<ParagraphAdapted>) : RecyclerView.Adapter<ParagraphViewHolder>() {
        val size: Point = Point()

        init {
            context.windowManager
                    .defaultDisplay
                    .getSize(size)
        }


        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ParagraphViewHolder?, p1: Int) {
            val item = items[p1]
            holder?.wordsView?.removeAllViews()
            val layoutParams = holder?.wordsView?.layoutParams
            layoutParams?.width = size.x / 4
            if (item.translated) {
                bindData(holder, item)
            } else {
                binder.translate(item, {
                    item.translated = true
                    bindData(holder, item)
                })
            }
        }

        private fun bindData(holder: ParagraphViewHolder?, item: ParagraphAdapted) {
            item.getWords().forEach {
                val word = it
                val rootView = WordView(context, word)
                rootView.setOnClickListener {
                    showWordDialog(word, {
                        removeWord(word)
                        binder.remove(word)
                    })
                }
                holder?.wordsView?.addView(rootView)
            }
            item.setOnWordClickListener { word ->
                showWordDialog(word, {
                    removeWord(word)
                    binder.remove(word)
                })
            }
            holder?.titleView?.setText(item.getAdapted(), TextView.BufferType.SPANNABLE)
        }

        private fun showWordDialog(word: WordAdapted, onIKnowClickListener: () -> Unit) {
            val definitions = word.getDefinitionsFull()
            if (definitions.isNotEmpty()) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(R.string.app_name)
                builder.setMessage(Html.fromHtml(definitions))
                        .setPositiveButton(R.string.i_know, { dialog, id ->
                            onIKnowClickListener()
                        })
                        .setNegativeButton(android.R.string.ok, null)
                val dialog = builder.create()
                dialog.show()
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
                    .inflate(R.layout.list_item_paragraph, p0, false)
            val vh = ParagraphViewHolder(view)
            return vh
        }
    }

    private class ParagraphViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val wordsView: ViewGroup

        init {
            titleView = view.findViewById(R.id.text_text) as TextView
            wordsView = view.findViewById(R.id.text_words) as ViewGroup
            titleView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}