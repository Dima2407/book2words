package org.book2dictionary.core.reader

import android.graphics.Bitmap
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import android.graphics.BitmapFactory
import android.text.TextUtils
import net.sf.jazzlib.ZipFile
import java.util.ArrayList
import nl.siegmann.epublib.domain.TOCReference


public class LocalReader(private val path: String) : BookReader {

    private var book: Book? = null

    private var title: String = ""

    private var author: String = ""

    override fun open() {
        book = EpubReader().readEpubLazy(ZipFile(path), "utf-8");
        title = book!!.getTitle()
        val authors = book!!.getMetadata().getAuthors()
        author = "${authors.get(0).getFirstname()} ${authors.get(0).getLastname()}"
    }

    override fun close() {
        book = null;
    }

    override fun release() {
        title = ""
        author = ""
    }

    override fun getTitle(): String {
        return title
    }

    override fun getAuthor(): String {
        return author
    }

    override fun getDisplayTitle(): String {
        val displayString = "${title}\n$author"
        return displayString;
    }

    override fun getCover(): Bitmap {
        val bitmap = BitmapFactory.decodeStream(book!!.getCoverImage().getInputStream());
        return bitmap;
    }

    override fun getTableOfContents(): List<String> {
        var list = ArrayList<String>();
        val tocReferences = book!!.getTableOfContents().getTocReferences()
        for (tocReference in tocReferences) {
            list.add(tocReference.getTitle())
        }
        return list;
    }
}
