package org.translate.core.yandex;

import com.google.gson.annotations.SerializedName;

class Mean {

    @SerializedName("text")
    private String text;

    @Override
    public String toString() {
        return text;
    }
}
