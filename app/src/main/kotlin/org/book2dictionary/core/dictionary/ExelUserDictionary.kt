package org.book2dictionary.core.dictionary

import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WriteException

import java.io.File
import java.util.TreeSet

class ExelUserDictionary(private val directory: String) : BaseUserDictionary() {
    private var bookToRead: Workbook? = null
    private var bookToWrite: WritableWorkbook? = null
    private var size = 0
    private var current = 0

    override fun prepare(write: Boolean) {
        try {
            val file = File(directory, BaseUserDictionary.USER_DICTIONARY + ".xls")
            if (file.exists()) {
                bookToRead = Workbook.getWorkbook(file)
                if (write) {
                    bookToWrite = Workbook.createWorkbook(file, bookToRead)
                }
                size = bookToRead!!.getSheet(SHEET).getRows()
            } else {
                bookToWrite = Workbook.createWorkbook(file)
                bookToWrite!!.createSheet(SHEET_NAME, SHEET)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun release() {
        if (bookToRead != null) {
            bookToRead!!.close()
            bookToRead = null
        }

        if (bookToWrite != null) {
            try {
                bookToWrite!!.write()
                bookToWrite!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            bookToWrite = null
        }
    }

    override fun sort() {
        val rows = bookToRead!!.getSheet(0).getColumn(0)
        val words = TreeSet<String>()
        for (i in 1..rows.size - 1) {
            val c = rows[i]
            val value = c.getContents().toLowerCase().trim()
            if (value.length() <= 1) {
                continue
            }
            words.add(value)
        }

        val sheet = bookToWrite!!.getSheet(0)
        size = 0
        for (value in words) {

            val label = Label(0, ++size, value)
            try {
                sheet.addCell(label)
            } catch (e: WriteException) {
                e.printStackTrace()
            }

        }

        run {
            var i = rows.size
            while (i > words.size()) {
                sheet.removeRow(i)
                i--
            }
        }

    }

    override fun contains(word: String): Boolean {
        val sheet = bookToRead!!.getSheet(0)
        val cell = sheet.findCell(word)
        return cell != null
    }

    override fun add(word: String): Boolean {
        val sheet = bookToWrite!!.getSheet(0)
        val label = Label(0, size++, word)
        try {
            sheet.addCell(label)
            return true
        } catch (e: WriteException) {
            e.printStackTrace()
        }

        return false
    }

    override fun iterator(): Iterator<String> {
        current = 0
        val sheet = bookToRead!!.getSheet(0)
        return object : Iterator<String> {
            override fun hasNext(): Boolean {
                return current < sheet.getRows()
            }

            override fun next(): String {
                val cell = sheet.getCell(0, current++)
                return cell.getContents()
            }
        }
    }

    class object {

        private val SHEET = 0
        private val SHEET_NAME = "WORDS"
    }

}
