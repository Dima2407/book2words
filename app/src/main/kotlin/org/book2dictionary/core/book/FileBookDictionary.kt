package org.book2dictionary.core.book

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileBookDictionary(directory: String, name: String, private val deleteExisted: Boolean) : BookDictionary {
    private val file: File
    private var fileWriter: FileWriter? = null
    private var fileReader: BufferedReader? = null

    {
        this.file = File(directory, name + ".txt")
    }

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

    override fun addChapter(name: String) {
        try {
            fileWriter!!.write(12.toChar().toInt())
            fileWriter!!.write("\n\n")
            fileWriter!!.write(name)
            fileWriter!!.write("\n\n")
            fileWriter!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun prepare(write: Boolean) {
        try {
            if (write) {
                fileWriter = FileWriter(file, !deleteExisted)
            } else {
                fileReader = BufferedReader(FileReader(file))
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

}
