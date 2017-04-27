package org.book2words.models

import org.book2words.core.Logger
import org.book2words.database.model.Part
import org.book2words.models.book.Word
import org.book2words.models.book.WordLocation
import java.io.File
import java.io.FileInputStream
import java.util.*


class TextSplitter private constructor() {

    private val capitals: MutableMap<String, Int> = LinkedHashMap()

    private val words: MutableMap<String, Word> = LinkedHashMap()

    private var allWordsCount = 0

    private var partitions = 0

    fun findCapital(text: String) {
        val wordPattern = Patterns.CAPITAL_WORD
        val matcher = wordPattern.matcher(text)
        var offset = 0
        while (matcher.find(offset)) {
            offset = matcher.start(1)
            val word = matcher.group(1).toLowerCase()
            val value = capitals.getOrElse(word, { 0 })
            capitals.put(word, value + 1)
            allWordsCount++
        }
        Logger.debug("capitals = ${capitals.size}")
    }

    fun toPartitions(bookId: Long, text: String): List<Part> {

        val parts = text.split("\n+".toRegex()).asSequence().filter { it.trim().isNotEmpty() }.mapIndexed { i, item ->
            var part = Part()
            part.bookId = bookId
            part.paragraphNumber = partitions++
            part.text = item
            part.amountOfSymbols = item.length
            part
        }
        return parts.toList()
    }


    fun split(partition: Part, bookId: Long) {
        val wordPattern = Patterns.WORD
        val matcher = wordPattern.matcher(partition.text)
        while (matcher.find()) {
            val w = matcher.group(1)
            if(w.isBlank()){
                continue
            }
            val start = matcher.start(1)
            val end = matcher.end(1)
            val word = words.getOrPut(w.toLowerCase(), {
                Word(w)
            })
            word.locations.add(WordLocation(bookId, partition.paragraphNumber, start, end));
            partition.amountOfWords++
        }
        allWordsCount += partition.amountOfWords;
    }

    fun clearCapital() {
        val words = capitals.filterValues { it >= Patterns.MAX_CAPITALS }
        clear {
            words.contains(it.toLowerCase())
        }
    }


    fun clearWidelyUsed(words: Array<String>) {
        clear {
            words.contains(it.toLowerCase())
        }
    }

    fun clearWithApostrophe() {
        val pattern = Patterns.WITH_APOSTROPHE
        clear {
            pattern.matcher(it).matches()
        }
    }


    fun clearWithDuplicates() {
        val pattern = Patterns.DUPLICATES
        clear {
            pattern.matcher(it).matches()
        }
    }

    private fun clear(condition: (input: String) -> Boolean) {
        Logger.debug("clear ${words.size}")
        val keys = words.filterKeys { condition(it) }
        keys.forEach {
            words.remove(it.key)
        }
        Logger.debug("cleared ${words.size}")
    }

    fun release() {
        words.clear()
        capitals.clear()
        partitions = 0
        allWordsCount = 0
    }

    companion object {
        private val splitter = TextSplitter()
        fun getInstance(): TextSplitter = splitter
    }

    fun clearFromDictionary(words : Set<String>) {
        if (words.isNotEmpty()) {
            clear {
                words.contains(it.toLowerCase())
            }
        }
    }

    fun getAllFoundWordsCount(): Int {
        return allWordsCount
    }

    fun getUniqueWordsCount(): Int {
        return capitals.size + words.size
    }

    fun getUnknownWordsCount(): Int {
        return words.size
    }

    fun getPartitionsCount(): Int {
        return partitions
    }

    fun getWords(): Collection<Word> {
        return words.values
    }
}