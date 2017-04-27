package org.book2words.models.split

import org.book2words.core.Logger

public class HtmlTagContentFetcher(private val onProcess: (text: String) -> Unit) : BodyTextFetcher {

    private val bodyPattern = "<body[^>]*>(.*)<\\/body>".toPattern()
    private val skipTag = "(<[^>]*>)|(<\\/[^>]+>)".toRegex()
    private val lineSeparator = """(</div>)|(</blockquote>)|(</p>)|(<br[^>]*>)|(&#[0-9]+;)""".toRegex()
    private val linesClearSeparator = "([^\\s])([\\s]*\n+[\\s]*)([^\\s])".toRegex()
    private val cssDefinitions = """([.#]?[-a-zA-z0-9]+\s[{][^{]+[}]\s*)+""".toRegex()

    override public fun processContent(text: String) {
        Logger.debug("processContent()")
        val matcher = bodyPattern.matcher(text)
        if (matcher.find()) {
            var body = matcher.group(1)
            body = body.replace(cssDefinitions, "")
            body = body.replace(lineSeparator, "\n")
            body = body.replace(skipTag, "")
            body = body.replace(linesClearSeparator, "$1\n$3")
            body = body.trim()
            onProcess(body)
        } else {
            onProcess("")
        }

    }
}
