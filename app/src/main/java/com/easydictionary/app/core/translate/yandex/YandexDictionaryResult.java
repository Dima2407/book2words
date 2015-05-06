package com.easydictionary.app.core.translate.yandex;

import com.easydictionary.app.core.translate.DictionaryResult;
import com.google.gson.annotations.SerializedName;

public class YandexDictionaryResult implements DictionaryResult {

    @SerializedName("def")
    private Definition[] definitions;

    @Override
    public String toString() {
        return takeResult();
    }

    @Override
    public String takeResult() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < definitions.length; i++) {
            sb.append(i + 1).append(") ").append(definitions[i].toString());
            if (i != definitions.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
