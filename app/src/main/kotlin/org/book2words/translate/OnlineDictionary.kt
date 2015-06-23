package org.book2words.translate

import com.google.gson.Gson
import org.book2words.core.Logger
import org.book2words.data.CacheDictionary
import org.book2words.translate.core.Definition
import org.book2words.translate.core.DictionaryResult
import org.book2words.translate.yandex.YDictionaryResult
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap
import java.util.concurrent.Executors

private class OnlineDictionary(private val dictionary: CacheDictionary,
                               private val from: String,
                               private val to: String) : Dictionary {

    private val deserializer = Gson()

    private val verbPast = "(\\w{3,})ed$".toPattern()

    private val plural = "(\\w{3,})s$".toPattern()

    private val plurals = "(\\w{3,})es$".toPattern()

    private val verbContinuous = "(\\w{3,})ing$".toPattern()

    private var executor = Executors.newSingleThreadExecutor();

    override fun find(input: String, onFound: (input: String, result: Array<out Definition>) -> Unit) {
        executor.submit({
            var defs = find(input)
            if(defs.isEmpty()){
                val matcher = verbPast.matcher(input)
                if(matcher.matches()){
                    defs = find(matcher.group(1));
                }
            }
            if(defs.isEmpty()){
                val matcher = plural.matcher(input)
                if(matcher.matches()){
                    defs = find(matcher.group(1))
                }
            }
            if(defs.isEmpty()){
                val matcher = plurals.matcher(input)
                if(matcher.matches()){
                    defs = find(matcher.group(1))
                }
            }
            if(defs.isEmpty()){
                val matcher = verbContinuous.matcher(input)
                if(matcher.matches()){
                    defs = find(matcher.group(1))
                }
            }
            onFound(input, defs);
        })
    }

    override fun find(input: String): Array<out Definition> {
        Logger.debug("find - ${input}", TAG)
        val result = translate(input)
        return result.getResults()
    }

    private fun translate(input: String): DictionaryResult {
        val cached = dictionary.take(input)
        if (cached != null) {
            Logger.debug("find at cache - ${input}", TAG)
            return deserializer.fromJson(cached,
                    javaClass<YDictionaryResult>())
        }
        val queryString = buildQueryString(input)
        Logger.debug("query string - ${queryString}", TAG)
        var result: DictionaryResult = YDictionaryResult()
        try {
            val url = URL(queryString)
            val connection = url.openConnection() as HttpURLConnection
            connection.setReadTimeout(10000)
            connection.setConnectTimeout(15000)
            connection.setRequestMethod("GET")
            connection.setDoInput(true)
            // Starts the query
            connection.connect()
            val responseCode = connection.getResponseCode()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    val reader = connection.getInputStream().bufferedReader(Charsets.UTF_8)
                    val resonse = StringBuilder()
                    reader.forEachLine {
                        resonse.append(it)
                    }
                    result = deserializer.fromJson(resonse.toString(),
                            javaClass<YDictionaryResult>())
                    if(result.getResults().isNotEmpty()) {
                        dictionary.save(input, resonse.toString())
                    }
                } catch(e: IOException) {
                    Logger.error(e)
                }
            }
        } catch(e: Exception) {
            Logger.error(e)
        }
        return result
    }

    private fun buildQueryString(input: String): String {
        val params = HashMap<String, Any>()
        params.put("key", API_KEY)
        params.put("lang", URLEncoder.encode("${from}-${to}", Charsets.UTF_8.name()))
        params.put("text", URLEncoder.encode(input, Charsets.UTF_8.name()))

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
        return queryString.toString()
    }

    companion object {

        private val TAG = javaClass<OnlineDictionary>().getSimpleName()
        private val API_KEY = "dict.1.1.20150121T133416Z.012b4f6033891237.6f0842fdef230f439d6de551d88d58831e4203e3";
        //private val API_KEY = "dict.1.1.20150106T111220Z.9a21fb953b9a84b1.b2c75f2ccb09ec04eff11a41590d35894dcf6124"

        private val QUERY = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
    }
}
