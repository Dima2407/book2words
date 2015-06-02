package org.book2words.screens.ui

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.book2words.R
import org.book2words.models.book.WordAdapted

public class WordView(context: Context) : LinearLayout(context) {

    private val wordView: TextView

    private val progressView: View

    private val saveView: CheckBox

    private var word: WordAdapted? = null

    private var onWordSaveListener: ((view: View, word: WordAdapted?) -> Unit)? = null

    init {
        View.inflate(context, R.layout.list_item_word, this)
        setOrientation(LinearLayout.VERTICAL)
        setPadding(6, 6, 6, 6)
        wordView = findViewById(R.id.text_word) as TextView
        saveView = findViewById(R.id.button_save) as CheckBox
        progressView = findViewById(R.id.progress_loading)

        saveView.setOnCheckedChangeListener { button, value ->
            if (value) {
                if (onWordSaveListener != null) {
                    onWordSaveListener!!(this, word)
                }
            }
        }
    }

    public fun setWord(word: WordAdapted) {
        this.word = word
        this.wordView.setText(word.word)
        this.wordView.setBackgroundColor(word.color)
        showDefinitions()
    }

    public fun showDefinitions() {
        if (word!!.translated) {
            val definitions = word!!.getDefinitions()
            if (definitions != null && definitions.isNotEmpty()) {
                this.wordView.setText("")
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
                this.wordView.append(Html.fromHtml(content.toString()))
            }
            stopWaiting()
        } else {
            startWaiting()
        }
    }

    public fun startWaiting() {
        this.progressView.setVisibility(View.VISIBLE)
        this.saveView.setVisibility(View.GONE)
    }

    public fun stopWaiting() {
        this.progressView.setVisibility(View.GONE)
        this.saveView.setVisibility(View.VISIBLE)
    }

    public fun setOnWordSaveListener(onWordSaveListener: ((view: View, word: WordAdapted?) -> Unit)?) {
        this.onWordSaveListener = onWordSaveListener
    }
}
