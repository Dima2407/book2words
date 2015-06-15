package org.book2words.core

import android.os.Environment
import org.book2words.dao.LibraryBook

import java.io.File

public class FileStorage {
    companion object {

        public fun createCoverFile(id: Long, extension: String): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            root.mkdirs()

            return File(root, "${id}${extension}")
        }

        public fun createChapterFile(bookId: Long, index: Int): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")
            root.mkdirs()

            return File(root, "${index}.partition")
        }

        public fun createDictionaryFile(book: LibraryBook): File {
            return createDictionaryFile(book.getDictionaryName(), book.getLanguage())
        }


        public fun createDictionaryFile(id: String, language : String): File {
            return createDictionaryFile("${id}.${language}")
        }

        public fun createDictionaryFile(fileName: String): File {
            val root = createDictionaryDirectory()

            return File(root, fileName)
        }

        public fun getDictionaryNameFromFile(id: String): String {
            return File(id).nameWithoutExtension
        }

        public fun createExportFile(): File {
            val root = File(Environment.getExternalStorageDirectory(), "b2w.zip")

            return root
        }

        public fun createDictionaryDirectory(): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}dictionaries")
            root.mkdirs()

            return root
        }

        public fun createWordsFile(bookId: Long): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")
            root.mkdirs()

            return File(root, "book.words")
        }

        public fun clearBook(bookId: Long) {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${bookId}")

            if (root.exists()) {
                root.listFiles().forEach {
                    it.delete()
                }
            }
        }

        public fun clearCovers() {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            if (root.exists()) {
                val files = root.listFiles()
                for (f in files) {
                    f.delete()
                }
            }
        }

        public fun imageCoverUri(id: Long): String {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            var file = File(root, "${id}.jpg")
            if (file.exists()) {
                return "file://${file.getAbsolutePath()}"
            } else {
                file = File(root, "${id}.png")
                if (file.exists()) {
                    return "file://${file.getAbsolutePath()}"
                }
            }
            return ""
        }

        public fun deleteCover(id: Long) {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            root.mkdirs()
            val pattern = "${id}\\.(png|jpg)"
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
