package org.book2words.screens.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import org.book2words.R
import org.book2words.translate.core.Definition

public class DefinitionView(context: Context) : LinearLayout(context) {

    private val wordView: TextView

    private val transcriptionView: TextView

    private val translateView: TextView

    init {
        View.inflate(context, R.layout.list_item_definition, this)
        setOrientation(LinearLayout.VERTICAL)
        wordView = findViewById(R.id.text_word) as TextView
        transcriptionView = findViewById(R.id.text_transcription) as TextView
        translateView = findViewById(R.id.text_translate) as TextView
    }

    public fun setDefinition(definition: Definition) {
        wordView.setText(definition.getText())
        transcriptionView.setText(definition.getTranscription())
        translateView.setText(definition.getTranslate())
    }
}
