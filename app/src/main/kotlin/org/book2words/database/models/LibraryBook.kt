package org.book2words.database.models

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

class LibraryBook(var id: Long? = null,
                  val name: String,
                  val authors: String,
                  var adapted: Int = LibraryBook.NONE,
                  var currentPartition: Int = 0,
                  var visibleParagraph: Int = 0,
                  var countPartitions: Int = 0,
                  var wordsCount: Int = 0,
                  var uniqueWordsCount: Int = 0,
                  var unknownWordsCount: Int = 0,
                  val language: String,
                  val path: String) : Parcelable {


    private constructor(`in`: Parcel) :
            this(`in`.readLong(),
                    `in`.readString(),
                    `in`.readString(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readInt(),
                    `in`.readString(),
                    `in`.readString())

    public constructor(cursor: Cursor, vararg indexes: Int ):
            this(cursor.getLong(indexes[0]),
                    cursor.getString(indexes[1]),
                    cursor.getString(indexes[2]),
                    cursor.getInt(indexes[3]),
                    cursor.getInt(indexes[4]),
                    cursor.getInt(indexes[5]),
                    cursor.getInt(indexes[6]),
                    cursor.getInt(indexes[7]),
                    cursor.getInt(indexes[8]),
                    cursor.getInt(indexes[9]),
                    cursor.getString(indexes[10]),
                    cursor.getString(indexes[11]))

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as LibraryBook?

        return path == that!!.path

    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id!!)
        dest.writeString(name)
        dest.writeString(authors)
        dest.writeInt(adapted)
        dest.writeInt(currentPartition)
        dest.writeInt(visibleParagraph)
        dest.writeInt(countPartitions)
        dest.writeInt(wordsCount)
        dest.writeInt(uniqueWordsCount)
        dest.writeInt(unknownWordsCount)
        dest.writeString(language)
        dest.writeString(path)
    }

    override fun toString(): String {
        return "$name\n$authors"
    }

    companion object {

        val NONE = 0
        val ADAPTING = 1
        val ADAPTED = 2
        @JvmField @SuppressWarnings("unused")
        val CREATOR : Parcelable.Creator<LibraryBook> = object : Parcelable.Creator<LibraryBook> {

            override fun createFromParcel(source: Parcel): LibraryBook {
                return LibraryBook(source)
            }

            override fun newArray(size: Int): Array<LibraryBook?> {
                return arrayOfNulls(size)
            }
        }
    }

}
