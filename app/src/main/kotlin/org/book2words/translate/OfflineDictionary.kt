package org.book2words.translate

import android.app.Service
import org.book2words.data.DataContext
import org.book2words.translate.core.Definition
import java.util.concurrent.Executors

class OfflineDictionary(private val context: Service) : Dictionary {

    private var executor = Executors.newSingleThreadExecutor()

    override fun find(input: String, onFound: (input: String, result: Array<out Definition>) -> Unit) {
        executor.submit({

            onFound(input, find(input))
        })
    }

    override fun find(input: String): Array<out Definition> {
        var items = findInternal(input)
        if (items.isEmpty()) {
            val forms = forms(input)
            for (item in forms) {
                items = findInternal(item)
                if (items.isNotEmpty()) {
                    break
                }
            }
        }
        return items
    }

    private fun findInternal(input: String): Array<Definition> {
        val words = DataContext.getDictionaryDao(context).findWordDefinitions(input)
        return words.toTypedArray()
    }

    companion object {

        private val TAG = OfflineDictionary::class.simpleName
    }
}
