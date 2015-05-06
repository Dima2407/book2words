package org.book2dictionary.core.reader

import android.graphics.Bitmap

public trait BookReader {

    public fun open()
    public fun close()

    public fun getTitle(): String
    public fun getDisplayTitle(): String
    public fun getCover(): Bitmap

    public fun getTableOfContents(): List<String>

    class object {
        public fun create(path: String): BookReader {
            return LocalReader(path);
        }
    }
}
