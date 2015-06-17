package org.book2words.screens.ui

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.TextUtils
import android.widget.TextView
import org.book2words.models.book.WordAdapted

public class WordView(context: Context, word: WordAdapted) : TextView(context) {

    init {
        setPadding(6, 6, 6, 6)
        setMaxLines(2)
        setTextColor(Color.BLACK)
        setEllipsize(TextUtils.TruncateAt.END)
        setBackgroundColor(word.getColor())
        setText(Html.fromHtml(word.getDefinitionsShort()))
    }
}
