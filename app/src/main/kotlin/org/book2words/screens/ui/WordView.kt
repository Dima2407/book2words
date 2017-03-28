package org.book2words.screens.ui

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.TextUtils
import android.widget.TextView
import org.book2words.models.book.WordAdapted

class WordView(context: Context, word: WordAdapted) : TextView(context) {

    init {
        setPadding(6, 6, 6, 6)
        maxLines = 2
        setTextColor(Color.BLACK)
        ellipsize = TextUtils.TruncateAt.END
        setBackgroundColor(word.getColor())
        text = Html.fromHtml(word.getDefinitionsShort())
    }
}
