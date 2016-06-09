package org.book2words.translate

import android.content.Context
import org.book2words.data.CacheDictionary
import org.book2words.translate.core.Definition
import java.util.ArrayList

public interface Dictionary {
    public fun find(input: String, onFound: (input: String, definitions: Array<out Definition>) -> Unit)

    public fun find(input: String): Array<out Definition>

    fun forms(input: String): List<String> {
        val forms = ArrayList<String>()
        var matcher = VERB_PAST.matcher(input)
        if (matcher.matches()) {
            forms.add(matcher.group(2))
            forms.add(matcher.group(1))
        }
        matcher = VERB_CONTINUOUS.matcher(input)
        if (matcher.matches()) {
            forms.add(matcher.group(1))
        }
        matcher = PLURALS_S.matcher(input)
        if (matcher.matches()) {
            forms.add(matcher.group(1))
        }
        matcher = PLURALS_ES.matcher(input)
        if (matcher.matches()) {
            forms.add(matcher.group(1))
        }
        matcher = PLURALS_IES.matcher(input)
        if (matcher.matches()) {
            forms.add("${matcher.group(1)}y")
        }
        return forms
    }

    companion object {
        private val TARGET = "ru"
        public fun createOnline(cache: CacheDictionary, from: String): Dictionary {
            return OnlineDictionary(cache, from, TARGET)
        }

        public fun createVerbs(context: Context): Dictionary {
            return VerbsDictionary(context.getResources())
        }

        public fun createOffline(context: Context): Dictionary {
            return OfflineDictionary(context.getResources())
        }

        val VERB_PAST = "((\\w{3,})e)d$".toPattern()

        val PLURALS_S = "(\\w{2,})s$".toPattern()

        val PLURALS_ES = "(\\w{2,})es$".toPattern()

        val PLURALS_IES = "(\\w{2,})(i)es$".toPattern()

        val VERB_CONTINUOUS = "(\\w{2,})ing$".toPattern()
    }
}
