package org.book2words.models.split

import org.book2words.core.Logger

public class HtmlTagContentFetcher(private val onProcess: (text: String) -> Unit) : BodyTextFetcher {

    private val bodyPattern = "<body[^>]*>(.*)<\\/body>".toPattern()
    private val skipTag = "(<[^>]*>)|(<\\/[^>]+>)".toRegex()
    private val lineSeparator = "(<\\/div>)|(<\\/blockquote>)|(<\\/p>)|(<br[^>]*>)".toRegex()
    private val linesClearSeparator = "([^\\s])([\\s]*\n+[\\s]*)([^\\s])".toRegex()

    override public fun processContent(text: String) {
        Logger.debug("processContent()")
        val matcher = bodyPattern.matcher(text)
        if (matcher.find()) {
            var body = matcher.group(1)
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
