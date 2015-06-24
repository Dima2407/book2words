package org.book2words.translate

import android.content.res.Resources
import org.book2words.R
import org.book2words.core.Logger
import org.book2words.translate.core.Definition
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.util.concurrent.Executors
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

public class OfflineDictionary(private val resources: Resources) : Dictionary {

    private val xPath = XPathFactory.newInstance().newXPath()

    private var executor = Executors.newSingleThreadExecutor()

    private val map = mapOf(
            "a" to R.raw.a,
            "b" to R.raw.b,
            "c" to R.raw.c,
            "d" to R.raw.d,
            "e" to R.raw.e,
            "f" to R.raw.f,
            "g" to R.raw.g,
            "h" to R.raw.h, /*
            "i" to R.raw.i,*/
            "j" to R.raw.j,
            "k" to R.raw.k,
            "l" to R.raw.l,
            "m" to R.raw.m,
            "n" to R.raw.n,
            "o" to R.raw.o,
            "p" to R.raw.p,
            "q" to R.raw.q,
            "r" to R.raw.r/*,
            "s" to R.raw.s,
            "t" to R.raw.t,
            "u" to R.raw.u,
            "v" to R.raw.v,
            "w" to R.raw.w,
            "x" to R.raw.x,
            "y" to R.raw.y,
            "z" to R.raw.z*/)

    override fun find(input: String, onFound: (input: String, result: Array<out Definition>) -> Unit) {
        executor.submit({

            onFound(input, find(input));
        })
    }

    override fun find(input: String): Array<out Definition> {
        val resourceId = map.get(input[0])
        var items = arrayOf<Definition>()
        if (resourceId != null) {
            items = findInternal(input, resourceId)
            if (items.isEmpty()) {
                val forms = forms(input)
                Logger.debug("forms - ${forms}", TAG)
                for (item in forms) {
                    items = findInternal(item, resourceId)
                    if (items.isNotEmpty()) {
                        break
                    }
                }
            }
        }
        return items
    }

    private fun findInternal(input: String, resourceId: Int): Array<Definition> {
        Logger.debug("find : ${input}", TAG)
        val source = InputSource(resources.openRawResource(resourceId))
        val expression = "/defs/d[@v='${input}']"
        val nodes = xPath.evaluate(expression, source, XPathConstants.NODESET) as NodeList?
        var items = arrayOf<Definition>()
        if (nodes != null) {
            items = Array(nodes.getLength(), {
                val node = nodes.item(it)
                WordDefinition(node)
            })
        }
        return items
    }

    private class WordDefinition(definition: Node) : Definition {
        private val text: String
        private var pos: String
        private var transcription: String
        private var translates: String

        init {
            val attributes = definition.getAttributes()
            text = attributes.getNamedItem("v").getNodeValue()
            transcription = attributes.getNamedItem("ts").getNodeValue()
            translates = attributes.getNamedItem("tr").getNodeValue()
            pos = attributes.getNamedItem("p").getNodeValue()
        }

        override fun getText(): String {
            return text
        }

        override fun getTranscription(): String {
            return transcription
        }

        override fun getPos(): String {
            return pos
        }

        override fun getTranslate(): String {
            return translates
        }
    }

    companion object {

        private val TAG = javaClass<OfflineDictionary>().getSimpleName()
    }
}
