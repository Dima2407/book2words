package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName
import org.book2words.translate.core.Definition

class YDefinition : Definition {

    @SerializedName("text")
    private var text: String = ""

    @SerializedName("pos")
    private var pos: String = ""

    @SerializedName("ts")
    private var transcription: String = ""

    @SerializedName("tr")
    private var translates: Array<Translate> = arrayOf()

    override fun getText(): String {
        return text
    }

    override fun getTranscription(): String {
        return transcription
    }

    override fun getTranslate(): String {
        val result = StringBuilder()
        translates.forEachIndexed { i, translate ->
            result.append(translate.text)
            if (i < translates.size - 1 ) {
                result.append("; ")
            }
        }
        return result.toString()
    }

    override fun getPos(): String {
        return pos
    }
}
