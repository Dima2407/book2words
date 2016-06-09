package org.book2words.translate.yandex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Translate {
    @SerializedName("text")
    var text: String = ""

    @SerializedName("pos")
    var pos: String = ""

    //SerializedName("mean")
    @Expose
    var means: Array<Mean>? = null

    //SerializedName("syn")
    @Expose
    var syns: Array<Syn>? = null

    //SerializedName("ex")
    @Expose
    var examples: Array<Example>? = null
}
