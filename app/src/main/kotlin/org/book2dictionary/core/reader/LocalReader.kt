package org.book2dictionary.core.reader

import android.graphics.Bitmap
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import android.graphics.BitmapFactory
import java.util.ArrayList
import nl.siegmann.epublib.domain.TOCReference


public class LocalReader(private val path: String) : BookReader {

    private var book: Book? = null;

    override fun open() {
        book = EpubReader().readEpubLazy(path, "utf-8");
    }

    override fun close() {
        book = null;
    }

    override fun getTitle(): String {
        return book!!.getTitle();
    }

    override fun getDisplayTitle(): String {
        val title = book!!.getTitle()
        val author = book!!.getMetadata().getAuthors()
        val displayString = "$title\n$author\n"
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
