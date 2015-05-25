package org.book2words.translate

import android.os.Handler
import android.os.HandlerThread
import com.google.gson.Gson
import org.apache.http.impl.client.DefaultHttpClient
import org.book2words.core.Logger
import org.book2words.translate.core.DictionaryResult
import org.book2words.translate.yandex.YandexDictionaryResult
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap

private class YandexTranslateProvider(private val from: String, private val to: String) : TranslateProvider {

    private val client = DefaultHttpClient()
    private val gson: Gson

    private var handler: Handler? = null
    private val handlerThread: HandlerThread

    {
        this.handlerThread = HandlerThread("YandexTranslateProvider")
        this.gson = Gson()
    }

    override fun translate(input: String, onTranslated: (input: String, result: DictionaryResult?) -> Unit) {

        if (handler == null && !handlerThread.isAlive()) {
            handlerThread.start()
            handler = Handler(handlerThread.getLooper())
        }

        handler!!.post(object : Runnable {
            override fun run() {
                val result = translate(input)
                onTranslated(input, result)
            }
        })

    }

    private fun translate(input: String): DictionaryResult? {
        val params = HashMap<String, Any>()
        params.put("key", API_KEY)
        params.put("lang", URLEncoder.encode("${from}-${to}", "utf-8"))
        params.put("text", URLEncoder.encode(input, "utf-8"))

        val queryString = StringBuilder(QUERY + "?")

        var index = 0
        params.forEach {
            queryString.append(it.getKey())
            queryString.append("=")
            queryString.append(it.getValue())
            if (index < params.size() - 1) {
                queryString.append("&")
            }
            index++
        }
        Logger.debug(queryString.toString())
        val url = URL(queryString.toString());
        val connection = url.openConnection() as HttpURLConnection
        connection.setReadTimeout(10000)
        connection.setConnectTimeout(15000)
        connection.setRequestMethod("GET")
        connection.setDoInput(true)
        // Starts the query
        connection.connect();
        val responseCode = connection.getResponseCode();
        var result: YandexDictionaryResult? = null
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                result = gson.fromJson<YandexDictionaryResult>(reader, javaClass<YandexDictionaryResult>())
            } catch(e: IOException) {
                Logger.error(e)
            }
        }
        return result
    }

    companion object {

        //private static final String API_KEY ="dict.1.1.20150121T133416Z.012b4f6033891237.6f0842fdef230f439d6de551d88d58831e4203e3";
        private val API_KEY = "dict.1.1.20150106T111220Z.9a21fb953b9a84b1.b2c75f2ccb09ec04eff11a41590d35894dcf6124"

        private val QUERY = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
    }
}
