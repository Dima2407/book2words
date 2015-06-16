package org.book2words.screens.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.TextUtils
import android.widget.TextView
import org.book2words.R
import org.book2words.models.book.WordAdapted

public class WordView(context: Context) : TextView(context) {

    private var word: WordAdapted? = null

    private var onWordClickListener: ((word: WordAdapted?) -> Unit)? = null

    init {
        setPadding(6, 6, 6, 6)
        setMaxLines(2)
        setTextColor(Color.BLACK)
        setEllipsize(TextUtils.TruncateAt.END)
        this.setOnClickListener {
            val definitions = word!!.getDefinitions()
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
                val builder = AlertDialog.Builder(getContext())
                builder.setTitle(R.string.app_name)
                builder.setMessage(Html.fromHtml(content.toString()))
                        .setPositiveButton(R.string.i_know, { dialog, id ->
                            if (onWordClickListener != null) {
                                onWordClickListener!!(word)
                            }
                        })
                        .setNegativeButton(android.R.string.ok, null);
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    public fun setWord(word: WordAdapted) {
        this.word = word
        setBackgroundColor(word.color)
        showDefinitions()
    }

    private fun showDefinitions() {
        val definitions = word!!.getDefinitions()
        val content = StringBuilder()
        definitions!!.forEachIndexed { i, it ->
            content.append("<i>")
            content.append(it.getTranslateShort())
            content.append("</i>")
            if (i < definitions.size() - 1) {
                content.append("<br/>")
            }
        }
        setText(Html.fromHtml(content.toString()))
    }

    public fun setOnWordClickListener(onWordClickListener: ((word: WordAdapted?) -> Unit)?) {
        this.onWordClickListener = onWordClickListener
    }
}
