package org.book2words.translate

import android.os.Handler
import android.os.HandlerThread
import com.google.gson.Gson
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.book2words.translate.core.DictionaryResult
import org.book2words.translate.yandex
import org.book2words.translate.yandex.YandexDictionaryResult

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.LinkedList

private class YandexTranslateProvider(private val from: String, private val to: String) : TranslateProvider {

    private val client = DefaultHttpClient()
    private val gson: Gson

    private var handler: Handler? = null
    private val handlerThread: HandlerThread

    {
        this.handlerThread = HandlerThread("YandexTranslateProvider")
        this.gson = Gson()
    }

    override fun translate(input: String, onTranslated:  (input: String, result: DictionaryResult?) -> Unit) {

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
        val params = LinkedList<NameValuePair>()
        params.add(BasicNameValuePair("key", API_KEY))
        params.add(BasicNameValuePair("lang", "${from}-${to}"))
        params.add(BasicNameValuePair("text", input))

        val paramString = URLEncodedUtils.format(params, "utf-8")

        val request = HttpGet(QUERY + "?" + paramString)

        var result: YandexDictionaryResult? = null
        try {
            val response = client.execute(request)
            val reader = BufferedReader(InputStreamReader(response.getEntity().getContent()))
            result = gson.fromJson<YandexDictionaryResult>(reader, javaClass<YandexDictionaryResult>())

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

    companion object {

        //private static final String API_KEY ="dict.1.1.20150121T133416Z.012b4f6033891237.6f0842fdef230f439d6de551d88d58831e4203e3";
        private val API_KEY = "dict.1.1.20150106T111220Z.9a21fb953b9a84b1.b2c75f2ccb09ec04eff11a41590d35894dcf6124"

        private val QUERY = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
    }
}
