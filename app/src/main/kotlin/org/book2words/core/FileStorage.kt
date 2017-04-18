package org.book2words.core

import android.os.Environment
import org.book2words.database.model.LibraryBook
import org.book2words.models.LibraryDictionary

import java.io.File

class FileStorage {
    companion object {

        fun createCoverFile(id: Long, extension: String): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            root.mkdirs()

            return File(root, "${id}${extension}")
        }

        fun createChapterFile(bookId: Long, index: Int): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")
            root.mkdirs()

            return File(root, "${index}.partition")
        }

        fun createDictionaryFile(book: LibraryBook): File {
            return createDictionaryFile(File(book.path).nameWithoutExtension, "en")
        }

        fun createDictionaryFile(item: LibraryDictionary): File {
            return createDictionaryFile(item.name, "en")
        }

        private fun createDictionaryFile(id: String, language: String): File {
            return createDictionaryFile("${id}.${language}")
        }

        fun createDictionaryFile(fileName: String): File {
            val root = createDictionaryDirectory()

            val file = File(root, fileName)
            println(file.absolutePath)
            if (!file.exists()) {
                //file.createNewFile()
            }
            return file
        }

        fun getDictionaryNameFromFile(id: String): String {
            return File(id).nameWithoutExtension
        }

        fun createExportFile(): File {
            val root = File(Environment.getExternalStorageDirectory(), "b2w.zip")

            return root
        }

        fun createDictionaryDirectory(): File {
            val root = File(Environment.getExternalStorageDirectory(), "Book2Words${File.separator}dictionaries")
            root.mkdirs()

            return root
        }

        fun createWordsFile(bookId: Long): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")
            root.mkdirs()

            return File(root, "book.words")
        }

        fun clearBook(bookId: Long) {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")

            if (root.exists()) {
                root.listFiles().forEach {
                    it.delete()
                }
            }
        }

        fun clearCovers() {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            if (root.exists()) {
                val files = root.listFiles()
                for (f in files) {
                    f.delete()
                }
            }
        }

        fun imageCoverUri(id: Long): String {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            var file = File(root, "${id}.jpg")
            if (file.exists()) {
                return "file://${file.absolutePath}"
            } else {
                file = File(root, "${id}.png")
                if (file.exists()) {
                    return "file://${file.absolutePath}"
                }
            }
            return ""
        }

        fun deleteCover(id: Long) {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            root.mkdirs()
            val pattern = "${id}\\.(png|jpg)".toRegex()
            var files = root.listFiles({ file, name ->
                name.matches(pattern)
            })
            if (files != null) {
                files.forEach {
                    it.delete()
                }
            }
        }
    }
}
