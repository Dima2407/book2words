package org.book2words.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import android.os.Parcel;
import android.os.Parcelable;
import org.book2words.core.FileStorage;

import java.io.File;
// KEEP INCLUDES END
/**
 * Entity mapped to table LIBRARY_DICTIONARY.
 */
public class LibraryDictionary implements Parcelable {

    // KEEP FIELDS - put your custom fields here
    public static final String ACTION_CREATED = "org.book2words.intent.action.DICTIONARY_CREATED";
    public static final String ACTION_UPDATED = "org.book2words.intent.action.DICTIONARY_UPDATED";
    public static final String ACTION_DELETED = "org.book2words.intent.action.DICTIONARY_DELETED";
    public static final Parcelable.Creator<LibraryDictionary> CREATOR = new Parcelable.Creator<LibraryDictionary>() {

        @Override
        public LibraryDictionary createFromParcel(Parcel source) {
            return new LibraryDictionary(source);
        }

        @Override
        public LibraryDictionary[] newArray(int size) {
            return new LibraryDictionary[size];
        }
    };
    private Long id;
    /** Not-null value. */
    private String name;
    private boolean use;
    private boolean readonly;
    /** Not-null value. */
    private String language;
    // KEEP FIELDS END
    private int size;

    public LibraryDictionary() {
    }

    public LibraryDictionary(Long id) {
        this.id = id;
    }

    public LibraryDictionary(Long id, String name, boolean use, boolean readonly, String language, int size) {
        this.id = id;
        this.name = name;
        this.use = use;
        this.readonly = readonly;
        this.language = language;
        this.size = size;
    }

    private LibraryDictionary(Parcel in) {
        id = in.readLong();
        name = in.readString();
        size = in.readInt();
        use = in.readInt() != 0;
        readonly = in.readInt() != 0;
        language = in.readString();
    }

    public LibraryDictionary(String name, String language) {
        this.name = name;
        this.language = language;
        this.use = true;
        this.readonly = false;
        this.size = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public boolean getUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /** Not-null value. */
    public String getLanguage() {
        return language;
    }

    // KEEP METHODS - put your custom methods here

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLanguage(String language) {
        this.language = language;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(size);
        dest.writeInt(use ? 1 : 0);
        dest.writeInt(readonly ? 1 : 0);
        dest.writeString(language);
    }

    public File getPath() {
        return FileStorage.Companion.createDictionaryFile(this);
    }
    // KEEP METHODS END

}
