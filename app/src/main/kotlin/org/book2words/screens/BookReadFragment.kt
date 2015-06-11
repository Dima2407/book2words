package org.book2words.screens

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
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
            holder!!.wordsView.removeAllViews()
            binder.translate(item, {
                item.getWords().forEach {
                    if (it.hasDefinition) {
                        val rootView = WordView(context)
                        rootView.setOnWordClickListener { word ->
                            removeWord(word!!)
                            binder.remove(word)
                        }
                        rootView.setWord(it)
                        holder.wordsView.addView(rootView)
                    }
                }
                item.setOnWordClickListener { word ->
                    showWordDialog(word!!, {
                        removeWord(word)
                        binder.remove(word)
                    })
                }
                holder!!.titleView.setText(item.getAdapted(), TextView.BufferType.SPANNABLE)
                holder!!.titleView.setMovementMethod(LinkMovementMethod.getInstance())
            })
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

        init {
            titleView = view.findViewById(R.id.text_text) as TextView
            wordsView = view.findViewById(R.id.text_words) as ViewGroup
        }
    }
}