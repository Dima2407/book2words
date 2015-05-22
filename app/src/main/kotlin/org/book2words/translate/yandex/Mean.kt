package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName

private class Mean : org.book2words.translate.core.Mean {

    SerializedName("text")
    private var text: String? = null

    override fun getText(): String? {
        return text
    }
}
