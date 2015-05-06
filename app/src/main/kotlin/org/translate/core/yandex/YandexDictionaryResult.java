package org.translate.core.yandex;

import com.google.gson.annotations.SerializedName;
import org.translate.core.DictionaryResult;

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
