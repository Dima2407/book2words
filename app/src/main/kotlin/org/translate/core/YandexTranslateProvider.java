package org.translate.core;

import android.os.Handler;
import android.os.HandlerThread;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.translate.core.yandex.YandexDictionaryResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

class YandexTranslateProvider implements TranslateProvider {

    private final HttpClient client = new DefaultHttpClient();

    //private static final String API_KEY ="dict.1.1.20150121T133416Z.012b4f6033891237.6f0842fdef230f439d6de551d88d58831e4203e3";
    private static final String API_KEY = "dict.1.1.20150106T111220Z.9a21fb953b9a84b1.b2c75f2ccb09ec04eff11a41590d35894dcf6124";

    private static final String QUERY = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";

    private final String from;
    private final String to;
    private final Gson gson;

    private Handler handler;
    private final HandlerThread handlerThread;

    YandexTranslateProvider(String from, String to) {
        this.from = from;
        this.to = to;
        this.handlerThread = new HandlerThread("YandexTranslateProvider");
        this.gson = new Gson();
    }

    @Override
    public void translate(final String input, final TranslateHandler translateHandler) {

        if (handler == null && !handlerThread.isAlive()) {
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                DictionaryResult result = translate(input);
                translateHandler.onTranslate(input, result);
            }
        });

    }

    private YandexDictionaryResult translate(String input) {
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("key", API_KEY));
        params.add(new BasicNameValuePair("lang", String.format("%s-%s", from, to)));
        params.add(new BasicNameValuePair("text", input));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        HttpGet request = new HttpGet(QUERY + "?" + paramString);

        YandexDictionaryResult result = null;
        try {
            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            result = gson.fromJson(reader, YandexDictionaryResult.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
