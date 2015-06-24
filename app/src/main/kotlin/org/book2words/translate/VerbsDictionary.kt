package org.book2words.translate;

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

private class VerbsDictionary(private val resources: Resources) : Dictionary {

    private val xPath = XPathFactory.newInstance().newXPath()
    private var executor = Executors.newSingleThreadExecutor();

    override fun find(input: String, onFound: (String, Array<out Definition>) -> Unit) {
        executor.submit({
            onFound(input, find(input));
        })
    }

    override fun find(input: String): Array<out Definition> {
        val source = InputSource(resources.openRawResource(R.raw.wrong_verbs))
        val expression = "/defs/d[@v='${input}']"
        Logger.debug("find verb : ${input}", TAG)
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
            val time = attributes.getNamedItem("t")?.getNodeValue()
            if (time == "ps") {
                pos = "${pos} - past simple"
            } else if (time == "pp") {
                pos = "${pos} - past participle"
            }
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

        private val TAG = javaClass<VerbsDictionary>().getSimpleName()
    }
}
