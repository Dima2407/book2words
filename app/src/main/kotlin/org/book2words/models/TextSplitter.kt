package org.book2words.models

import org.book2words.core.Logger
import org.book2words.models.book.Word
import java.io.File
import java.io.FileInputStream
import java.util.LinkedHashSet
import java.util.TreeSet

public class TextSplitter private () {

    private val capitals = LinkedHashSet<String>();

    public val words: MutableSet<Word> = LinkedHashSet();
    private var size = 0;

    public fun findCapital(text: String) {
        val wordPattern = Patterns.CAPITAL_WORD;
        val matcher = wordPattern.matcher(text);
        var offset = 0;
        while (matcher.find(offset)) {
            offset = matcher.start(1);
            capitals.add(matcher.group(1).toLowerCase());
        }
        Logger.debug("capitals = ${capitals.size()}")

    }

    public fun split(key: Int, text: String, paragraphs: Int) {
        Logger.debug("split chapter ${key}")
        val wordPattern = Patterns.WORD;

        val parts = text.split("\n+");
        parts.forEachIndexed { i, item ->
            val matcher = wordPattern.matcher(item);
            while (matcher.find()) {
                val w = matcher.group(1)
                val start = matcher.start(1)
                val end = matcher.end(1)
                var word = words.firstOrNull {
                    it.value.equalsIgnoreCase(w)
                }
                if (word == null) {
                    word = Word(w)
                    words.add(word as Word)
                }
                word!!.addParagraph(i % paragraphs, key, i / paragraphs, start, end)
            }
        }
        Logger.debug("words = ${words.size()}");
        size++
    }

    public fun clearCapital() {
        clear {
            capitals.contains(it.toLowerCase())
        }
    }


    public fun clearWidelyUsed(words: Array<String>) {
        clear {
            words.contains(it.toLowerCase())
        }
    }

    public fun clearWithApostrophe() {
        val pattern = Patterns.WITH_APOSTROPHE;
        clear {
            pattern.matcher(it).matches()
        }
    }


    public fun clearWithDuplicates() {
        val pattern = Patterns.DUPLICATES;
        clear {
            pattern.matcher(it).matches()
        }
    }

    public fun clearWords(words: Set<String>) {
        clear {
            words.contains(it.toLowerCase())
        }
    }

    private fun clear(condition: (input: String) -> Boolean) {
        Logger.debug("clear ${words.size()}")
        val iterator = words.iterator()
        while (iterator.hasNext()) {
            val word = iterator.next();
            if (condition(word.value)) {
                Logger.debug("remove ${word}")
                iterator.remove();
            }
        }
        Logger.debug("clear ${words.size()}")
    }

    public fun release() {
        words.clear()
        capitals.clear()
        size = 0
    }

    companion object {
        private val splitter = TextSplitter()
        public fun getInstance(): TextSplitter = splitter
    }

    public fun clearFromDictionary(path: File) {
        val bos = FileInputStream(path).reader(Charsets.UTF_8).buffered()
        val words = TreeSet<String>()
        bos.forEachLine {
            words.add(it.toLowerCase())
        }
        bos.close()

        clear {
            words.contains(it.toLowerCase())
        }
    }

    public fun size(): Int {
        return size
    }
}