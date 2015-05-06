package org.book2dictionary.core.book


import android.text.TextUtils
import jxl.Cell
import jxl.Workbook
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.write.biff.RowsExceededException

import java.io.File
import jxl.write.WritableWorkbook
import jxl.write.WritableSheet
import jxl.write.WritableCellFormat
import jxl.write.Label
import jxl.write.WriteException

class ExelBookDictionary(directory: String, name: String, private val deleteExisted: Boolean) : BookDictionary {

    private var bookToWrite: WritableWorkbook? = null
    private var sheet: WritableSheet? = null

    private val file: File
    private var rowIndex: Int = 0
    private var chapterIndex: Int = 0
    private var bookToRead: Workbook? = null

    {
        this.file = File(directory, name + ".xls")
    }

    override fun prepare(write: Boolean) {
        try {
            if (file.exists() && deleteExisted) {
                file.delete()
            }
            if (file.exists()) {
                bookToRead = Workbook.getWorkbook(file)
                bookToWrite = Workbook.createWorkbook(file, bookToRead)
            } else {
                bookToWrite = Workbook.createWorkbook(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.chapterIndex = 0
        this.rowIndex = 0
    }

    override fun moveToChapter(chapterIndex: Int): Boolean {
        if (chapterIndex >= bookToWrite!!.getNumberOfSheets()) {
            return false
        }
        this.sheet = bookToWrite!!.getSheet(chapterIndex)
        this.chapterIndex = chapterIndex
        this.rowIndex = 0
        return true
    }

    override fun addChapter(name: String) {
        sheet = bookToWrite!!.createSheet(name, chapterIndex++)
        sheet!!.setColumnView(0, COLUMN_WIDTH)
        rowIndex = 0
    }

    override fun remove(word: String): Boolean {
        val cell = sheet!!.findCell(word)
        if (cell != null) {
            sheet!!.removeRow(cell.getRow())
            return true
        }
        return false
    }

    override fun add(word: String): Boolean {

        try {
            val cellFormat = WritableCellFormat()
            cellFormat.setWrap(true)
            cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.DASH_DOT_DOT)
            val label = Label(COLUMN_INDEX, rowIndex, word, cellFormat)

            try {
                if (!TextUtils.isEmpty(word)) {
                    val lines = word.split("\n")
                    var linesCount = lines.size
                    for (l in lines) {
                        if (l.length() > 190) {
                            //TODO: why??? if width 150
                            linesCount += 1
                        }
                    }
                    sheet!!.setRowView(rowIndex, linesCount * 20 * 12, false)
                } else {
                    sheet!!.setRowView(rowIndex, 20 * 12, false)
                }
            } catch (e: RowsExceededException) {
                e.printStackTrace()
            }

            sheet!!.addCell(label)
            rowIndex++
            return true
        } catch (e: WriteException) {
            e.printStackTrace()
        }

        return false
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

    override fun iterator(): Iterator<String> {
        rowIndex = 0
        return object : Iterator<String> {
            override fun hasNext(): Boolean {
                val next = rowIndex < sheet!!.getRows()
                if (!next) {
                    rowIndex = 0
                }
                return next
            }

            override fun next(): String {
                val cell = sheet!!.getCell(COLUMN_INDEX, rowIndex++)
                return cell.getContents()
            }
        }
    }

    class object {

        private val COLUMN_INDEX = 0
        private val COLUMN_WIDTH = 200
    }
}
