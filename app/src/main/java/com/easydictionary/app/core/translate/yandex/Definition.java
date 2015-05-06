package com.easydictionary.app.core.translate.yandex;

import com.google.gson.annotations.SerializedName;

class Definition {

    @SerializedName("text")
    private String text;

    @SerializedName("pos")
    private String pos;

    @SerializedName("ts")
    private String transcription;

    @SerializedName("tr")
    private Translate [] translates;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(text).append(" {").append(pos).append("} - ")
                .append("[").append(transcription).append("] - ");
        for (int i = 0; i < translates.length; i++) {
            Translate tr = translates[i];
            sb.append(tr);
            if(i != translates.length-1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
