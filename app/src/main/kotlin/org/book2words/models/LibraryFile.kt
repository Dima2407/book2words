package org.book2words.models

import android.net.Uri
import java.io.File
import java.io.FileFilter

public class LibraryFile private (val title: String, val path: String, val files: Int = -1) {

    public fun isBook(): Boolean {
        return files == -1
    }

    public fun asUri(): Uri {
        return Uri.fromFile(File(path))
    }

    override fun toString(): String {
        return "${title}"
    }

    companion object {

        private val EXTENSION = ".epub"

        public fun create(f: File): LibraryFile? {
            if (f.isHidden() || (f.isFile() && !f.getName().endsWith(EXTENSION))) {
                return null
            }

            var files = -1
            if (f.isDirectory()) {
                files = f.listFiles(object : FileFilter {
                    override fun accept(pathname: File): Boolean {
                        return pathname.isDirectory() || isBook(pathname)
                    }
                }).size()
                if (files == 0) {
                    return null
                }
            }
            return LibraryFile(f.getName(), f.getAbsolutePath(), files)
        }

        private fun isBook(file : File) : Boolean{
            return file.isFile() && file.getName().endsWith(EXTENSION)
        }
    }

}