package org.book2words.models

import org.book2words.core.Logger
import org.book2words.models.book.Partition
import org.book2words.models.book.Word
import java.io.File
import java.io.FileInputStream
import java.util.LinkedHashMap
import java.util.TreeMap
import java.util.TreeSet

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

    fun toPartitions(key: Int, text: String, partitionSize: Int): TreeMap<String, Partition> {

        Logger.debug("split chapter ${key}")

        val paragraphs = text.split("\n+".toRegex())
        val partitions = TreeMap<String, Partition>()
        paragraphs.forEachIndexed { i, item ->
            val p = "${key}-${i / partitionSize}"
            val partition = partitions.getOrPut(p, {
                Partition(p)
            })
            partition.add(item)
        }
        Logger.debug("chapter ${key} - contains ${partitions.size} partitions")
        return partitions
    }

    fun nextPartition() {
        partitions++
    }

    fun split(partition: Partition) {
        val wordPattern = Patterns.WORD
        partition.forEachIndexed { i, item ->
            val matcher = wordPattern.matcher(item)
            while (matcher.find()) {
                val w = matcher.group(1)
                val start = matcher.start(1)
                val end = matcher.end(1)
                var word = words.getOrPut(w.toLowerCase(), {
                    Word(w)
                })
                word.addParagraph(i, partitions, start, end)
                allWordsCount++
            }
        }
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

    fun clearFromDictionary(path: File) {
        if (path.exists()) {
            val bos = FileInputStream(path).bufferedReader(Charsets.UTF_8)
            val words = TreeSet<String>()
            bos.forEachLine {
                words.add(it.toLowerCase())
            }
            bos.close()

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