package org.translate.core.yandex;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

class Translate {
    @SerializedName("text")
    private String text;

    @SerializedName("pos")
    private String pos;

    @SerializedName("mean")
    private Mean[] means;

    @Override
    public String toString() {
        return text + " {" + pos + "}" + (means == null ? "" :
                " : " + Arrays.toString(means));
    }
}
