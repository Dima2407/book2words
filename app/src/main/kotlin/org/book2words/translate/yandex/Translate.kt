package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName
import org.book2words.translate.core

private class Translate : org.book2words.translate.core.Translate {
    SerializedName("text")
    private var text: String? = null

    SerializedName("pos")
    private var pos: String? = null

    SerializedName("mean")
    private var means: Array<Mean>? = null

    override fun getText(): String? {
        return text
    }

    override fun getMeans(): Array<out core.Mean>? {
        return means
    }
}
