package org.book2words.models.storage

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.book2words.models.storage.dao.EntityUpdatable

public class LibraryBook(SerializedName("name") val name: String,
                         SerializedName("authors") val authors: String,
                         SerializedName("path") val path: String,
                         SerializedName("adapted") var adapted: Boolean = false,
                         SerializedName("read") var read: Boolean = false,
                         SerializedName("current_partition") var currentPartition: Int = 0,
                         SerializedName("count_partitions") var countPartitions: Int = 0,
                         SerializedName("all_words") var allWords: Int = 0,
                         SerializedName("unique_words") var uniqueWords: Int = 0,
                         SerializedName("capital_words") var capitalWords: Int = 0,
                         SerializedName("unknown_words") var unknownWords: Int = 0) : Parcelable, EntityUpdatable<LibraryBook> {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(authors)
        dest.writeString(path)
        dest.writeInt(if (adapted) 1 else 0)
        dest.writeInt(if (read) 1 else 0)
        dest.writeInt(currentPartition)
        dest.writeInt(countPartitions)
        dest.writeInt(allWords)
        dest.writeInt(uniqueWords)
        dest.writeInt(capitalWords)
        dest.writeInt(unknownWords)
    }

    private constructor(input: Parcel) : this(input.readString(),
            input.readString(),
            input.readString(),
            input.readInt() == 1,
            input.readInt() == 1,
            input.readInt(),
            input.readInt(),
            input.readInt(),
            input.readInt(),
            input.readInt(),
            input.readInt()) {
    }

    override public fun update(item: LibraryBook) {
        this.adapted = item.adapted
        this.read = item.read
        this.currentPartition = item.currentPartition
        this.countPartitions = item.countPartitions
        this.allWords = item.allWords
        this.uniqueWords = item.uniqueWords
        this.capitalWords = item.capitalWords
        this.unknownWords = item.uniqueWords
    }

    override fun toString(): String {
        return "${name}\n${authors}"
    }

    override fun equals(o: Any?): Boolean {
        if (this == o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as LibraryBook

        return path == that.path

    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    companion object {

        public val CREATOR: Parcelable.Creator<LibraryBook> = object : Parcelable.Creator<LibraryBook> {

            override fun createFromParcel(source: Parcel): LibraryBook {
                return LibraryBook(source)
            }

            override fun newArray(size: Int): Array<LibraryBook?> {
                return arrayOfNulls(size)
            }
        }
    }

}
