package org.book2words.translate;

import android.content.res.Resources
import android.os.Handler
import android.os.HandlerThread
import org.book2words.R
import org.book2words.core.Logger
import org.book2words.translate.core.Definition
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

private class WrongVerbsDictionary(private val resources: Resources) : Dictionary {

    val xPath = XPathFactory.newInstance().newXPath()

    override fun find(input: String, onFound: (String, Array<out Definition>) -> Unit) {
        if (handler == null && !handlerThread.isAlive()) {
            handlerThread.start()
            handler = Handler(handlerThread.getLooper())
        }

        handler!!.post({
            val source = InputSource(resources.openRawResource(R.raw.wrong_verbs))
            val expression = "/defs/d[@v='${input}']"
            Logger.debug("wrong-verb : ${input}")
            val nodes = xPath.evaluate(expression, source, XPathConstants.NODESET) as NodeList?
            var items = arrayOf<Definition>()
            if (nodes != null) {
                items = Array(nodes.getLength(), {
                    val node = nodes.item(it)
                    VerbDefinition(node)
                })
            }
            onFound(input, items);
        })

    }

    private var handler: Handler? = null
    private val handlerThread: HandlerThread

    init {
        this.handlerThread = HandlerThread("WrongVerbsDictionary")
    }

    private class VerbDefinition(definition: Node) : Definition {
        private val text: String
        private var pos: String = ""
        private var transcription: String
        private var translates: String

        init {
            val attributes = definition.getAttributes()
            text = attributes.getNamedItem("v").getNodeValue()
            transcription = attributes.getNamedItem("ts").getNodeValue()
            translates = attributes.getNamedItem("tr").getNodeValue()
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
}
