package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName

private class Syn {

    SerializedName("text")
    var text: String = ""

    SerializedName("pos")
    var pos: String = ""
}