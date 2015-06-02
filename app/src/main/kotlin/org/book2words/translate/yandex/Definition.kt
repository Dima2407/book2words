package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName

private class Definition : org.book2words.translate.core.Definition {

    SerializedName("text")
    private var text: String? = null

    SerializedName("pos")
    private var pos: String? = null

    SerializedName("ts")
    private var transcription: String? = null

    SerializedName("tr")
    private var translates: Array<Translate>? = null

    override fun getText(): String? {
        return text
    }

    override fun getTranscription(): String? {
        return transcription
    }

    override fun getTranslate(): String? {
        val result = StringBuilder()
        translates!!.forEachIndexed { i, translate ->
            result.append(translate.getText())
            if (i < translates!!.size() - 1 ) {
                result.append("; ")
            }
        }
        return result.toString()
    }
}
