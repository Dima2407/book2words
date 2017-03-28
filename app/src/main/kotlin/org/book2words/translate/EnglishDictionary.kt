package org.book2words.translate

import android.app.Service
import org.book2words.data.DictionaryContext
import org.book2words.translate.core.Definition
import java.util.concurrent.Executors

public class EnglishDictionary(service: Service) : Dictionary {

    private val offline = OfflineDictionary(service.getResources())

    private val online = OnlineDictionary(DictionaryContext.getConfigs(service), "en", "ru")

    private val verbs = VerbsDictionary(service.getResources())

    private var executor = Executors.newFixedThreadPool(4)

    override fun find(input: String, onFound: (String, Array<out Definition>) -> Unit) {
        executor.execute {
            onFound(input, find(input))
        }
    }

    override fun find(input: String): Array<out Definition> {
        var definitions = verbs.find(input)
        if (definitions.isEmpty()) {
            definitions = offline.find(input)
        }
        if (definitions.isEmpty()) {
            definitions = online.find(input)
        }
        return definitions
    }
}
