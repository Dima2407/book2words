package org.book2dictionary.core.dictionary

import java.io.*

class FileUserDictionary(private val path: String) : BaseUserDictionary() {
    private var fileReader: BufferedReader? = null
    private var fileWriter: FileWriter? = null

    override fun add(word: String): Boolean {
        try {
            fileWriter!!.write(word)
            fileWriter!!.write("\n")
            fileWriter!!.flush()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun prepare(write: Boolean) {
        try {
            if (write) {
                fileWriter = FileWriter(File(path, BaseUserDictionary.USER_DICTIONARY + ".txt"), true)
            } else {
                fileReader = BufferedReader(FileReader(File(path, BaseUserDictionary.USER_DICTIONARY + ".txt")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun release() {
        try {
            if (fileWriter != null) {
                fileWriter!!.close()
            }
            if (fileReader != null) {
                fileReader!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun iterator(): Iterator<String> {
        return object : Iterator<String> {
            var line: String? = null
            override fun hasNext(): Boolean {
                line = null
                try {
                    line = fileReader!!.readLine()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return line != null
            }

            override fun next(): String {
                return line.orEmpty();
            }

        }
    }

    override fun sort() {
    }
}
