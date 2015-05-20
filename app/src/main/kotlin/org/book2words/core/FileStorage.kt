package org.book2words.core

import android.os.Environment

import java.io.File

public class FileStorage {
    companion object {

        public fun createCoverFile(id: Long, extension: String): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}covers")
            root.mkdirs()

            return File(root, "${id}${extension}")
        }

        public fun createChapterFile(id: Long, index: Int): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${id}")
            root.mkdirs()

            return File(root, "${index}.chapter")
        }

        public fun createWordsFile(id: Long): File {
            val root = File(Environment.getExternalStorageDirectory(), ".b2w${File.separator}books${File.separator}${id}")
            root.mkdirs()

            return File(root, "book.words")
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
    }
}
