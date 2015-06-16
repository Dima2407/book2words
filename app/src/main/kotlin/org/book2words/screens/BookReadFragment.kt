package org.book2words.screens

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.graphics.Point
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
        return inflater.inflate(R.layout.fragment_read, null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view?.findViewById(android.R.id.list) as RecyclerView
        progressView = view?.findViewById(R.id.frame_progress)
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

    private class ParagraphAdapter(
            private val context: Activity,
            private val binder: BookReaderBinder,
            private val items: List<ParagraphAdapted>) : RecyclerView.Adapter<ParagraphViewHolder>() {
        val size: Point = Point()

        init {
            context.getWindowManager()
                    .getDefaultDisplay()
                    .getSize(size)
        }


        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onBindViewHolder(holder: ParagraphViewHolder?, p1: Int) {
            val item = items[p1]
            holder?.wordsView?.removeAllViews()
            val layoutParams = holder?.wordsView?.getLayoutParams()
            layoutParams?.width = size.x / 3
            if (item.translated) {
                holder?.loadingView?.setVisibility(View.GONE)
                holder?.contentView?.setVisibility(View.VISIBLE)
                bindData(holder, item)
            } else {
                holder?.loadingView?.setVisibility(View.VISIBLE)
                holder?.contentView?.setVisibility(View.GONE)
                binder.translate(item, {
                    item.translated = true
                    holder?.loadingView?.setVisibility(View.GONE)
                    holder?.contentView?.setVisibility(View.VISIBLE)
                    bindData(holder, item)
                })
            }
        }

        private fun bindData(holder: ParagraphViewHolder?, item: ParagraphAdapted) {
            item.getRightWords().forEach {
                val rootView = WordView(context)
                rootView.setOnWordClickListener { word ->
                    removeWord(word!!)
                    binder.remove(word)
                }
                rootView.setWord(it)
                holder?.wordsView?.addView(rootView)
            }
            item.setOnWordClickListener { word ->
                showWordDialog(word!!, {
                    removeWord(word)
                    binder.remove(word)
                })
            }
            holder?.titleView?.setText(item.getAdapted(), TextView.BufferType.SPANNABLE)
        }

        private fun showWordDialog(word: WordAdapted, onIKnowClickListener: () -> Unit) {
            val definitions = word.getDefinitions()
            if (definitions != null && definitions.isNotEmpty()) {

                val content = StringBuilder()

                definitions.forEachIndexed { i, it ->
                    content.append(it.getText())
                    content.append(" - ")
                    content.append("<b>[ ")
                    content.append(it.getTranscription())
                    content.append(" ]</b>")
                    content.append(" - ")
                    content.append("<i>")
                    content.append(it.getTranslate())
                    content.append("</i>")
                    if (i < definitions.size() - 1) {
                        content.append("<br/>")
                    }
                }
                val builder = AlertDialog.Builder(context)
                builder.setTitle(R.string.app_name)
                builder.setMessage(Html.fromHtml(content.toString()))
                        .setPositiveButton(R.string.i_know, { dialog, id ->
                            onIKnowClickListener()
                        })
                        .setNegativeButton(android.R.string.ok, null);
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
                    .inflate(R.layout.list_item_paragraph, p0, false);
            val vh = ParagraphViewHolder(view);
            return vh;
        }
    }

    private class ParagraphViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val wordsView: ViewGroup
        val loadingView: View
        val contentView: View

        init {
            titleView = view.findViewById(R.id.text_text) as TextView
            wordsView = view.findViewById(R.id.text_words) as ViewGroup
            loadingView = view.findViewById(R.id.progress_loading)
            contentView = view.findViewById(R.id.content_frame)
            titleView.setMovementMethod(LinkMovementMethod.getInstance())
        }
    }
}