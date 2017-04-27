package org.book2words.models

import android.os.Parcel
import android.os.Parcelable
import org.book2words.core.FileStorage

import java.io.File

class LibraryDictionary(val name: String, var size: Int) : Parcelable {

    private constructor(`in`: Parcel) : this(`in`.readString(), `in`.readInt()) {

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(size)
    }

    companion object {

        val ACTION_MODIFIED = "org.book2words.intent.action.DICTIONARY_MODIFIED"

        @JvmField val CREATOR: Parcelable.Creator<LibraryDictionary> = object : Parcelable.Creator<LibraryDictionary> {

            override fun createFromParcel(source: Parcel): LibraryDictionary {
                return LibraryDictionary(source)
            }

            override fun newArray(size: Int): Array<LibraryDictionary?> {
                return arrayOfNulls(size)
            }
        }
    }

}
