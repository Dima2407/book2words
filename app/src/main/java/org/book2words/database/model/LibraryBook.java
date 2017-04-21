package org.book2words.database.model;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

// KEEP INCLUDES END

import android.os.Parcel;
import android.os.Parcelable;

import org.book2words.models.book.Partition;
import org.book2words.models.book.Word;

import java.util.List;

/**
 * Entity mapped to table LIBRARY_BOOK.
 */
public class LibraryBook implements Parcelable {

    // KEEP FIELDS - put your custom fields here
    public static final int NONE = 0;
    public static final int ADAPTING = 1;
    public static final int ADAPTED = 2;
    public static final Parcelable.Creator<LibraryBook> CREATOR = new Parcelable.Creator<LibraryBook>() {

        @Override
        public LibraryBook createFromParcel(Parcel source) {
            return new LibraryBook(source);
        }

        @Override
        public LibraryBook[] newArray(int size) {
            return new LibraryBook[size];
        }
    };
    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String authors;
    private List<Word> foundWords;
    private List<Partition> partitions;
    private int adapted;
    private int currentPartition;
    private int countPartitions;
    private int wordsCount;
    private int uniqueWordsCount;
    private int unknownWordsCount;
    /** Not-null value. */
    private String language;
    // KEEP FIELDS END
    /** Not-null value. */
    private String path;

    public LibraryBook() {
    }

    public LibraryBook(Long id) {
        this.id = id;
    }

    public LibraryBook(Long id, String name, String authors, int adapted, int currentPartition, int countPartitions, int wordsCount, int uniqueWordsCount, int unknownWordsCount, String language, String path) {
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.adapted = adapted;
        this.currentPartition = currentPartition;
        this.countPartitions = countPartitions;
        this.wordsCount = wordsCount;
        this.uniqueWordsCount = uniqueWordsCount;
        this.unknownWordsCount = unknownWordsCount;
        this.language = language;
        this.path = path;
    }

    public LibraryBook(Long id, String name, String authors, List<Word> foundWords, List<Partition> partitions, int adapted, int currentPartition, int countPartitions, int wordsCount, int uniqueWordsCount, int unknownWordsCount, String language, String path) {
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.foundWords = foundWords;
        this.partitions = partitions;
        this.adapted = adapted;
        this.currentPartition = currentPartition;
        this.countPartitions = countPartitions;
        this.wordsCount = wordsCount;
        this.uniqueWordsCount = uniqueWordsCount;
        this.unknownWordsCount = unknownWordsCount;
        this.language = language;
        this.path = path;
    }

        private LibraryBook(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.authors = in.readString();
        this.adapted = in.readInt();
        this.currentPartition = in.readInt();
        this.countPartitions = in.readInt();
        this.wordsCount = in.readInt();
        this.uniqueWordsCount = in.readInt();
        this.unknownWordsCount = in.readInt();
        this.language = in.readString();
        this.path = in.readString();
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

    /** Not-null value. */
    public String getAuthors() {
        return authors;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public List<Word> getFoundWords() {
        return foundWords;
    }

    public void setFoundWords(List<Word> words) {
        this.foundWords = words;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Partition> partitions) {
        this.partitions = partitions;
    }

  /*  public void addPartition(Partition partition){
        partitions.add(partition);
    }
*/
    public int getAdapted() {
        return adapted;
    }

    public void setAdapted(int adapted) {
        this.adapted = adapted;
    }

    public int getCurrentPartition() {
        return currentPartition;
    }

    public void setCurrentPartition(int currentPartition) {
        this.currentPartition = currentPartition;
    }

    public int getCountPartitions() {
        return countPartitions;
    }

    public void setCountPartitions(int countPartitions) {
        this.countPartitions = countPartitions;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    public int getUniqueWordsCount() {
        return uniqueWordsCount;
    }

    public void setUniqueWordsCount(int uniqueWordsCount) {
        this.uniqueWordsCount = uniqueWordsCount;
    }

    public int getUnknownWordsCount() {
        return unknownWordsCount;
    }

    public void setUnknownWordsCount(int unknownWordsCount) {
        this.unknownWordsCount = unknownWordsCount;
    }

    /** Not-null value. */
    public String getLanguage() {
        return language;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLanguage(String language) {
        this.language = language;
    }

    // KEEP METHODS - put your custom methods here

    /** Not-null value. */
    public String getPath() {
        return path;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPath(String path) {
        this.path = path;
    }

  /*  @Override
    public int describeContents() {
        return 0;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryBook that = (LibraryBook) o;

        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(authors);
        dest.writeInt(adapted);
        dest.writeInt(currentPartition);
        dest.writeInt(countPartitions);
        dest.writeInt(wordsCount);
        dest.writeInt(uniqueWordsCount);
        dest.writeInt(unknownWordsCount);
        dest.writeString(language);
        dest.writeString(path);
    }

    public String getDictionaryName(){
        return name + " - " + authors;
    }
    @Override
    public String toString() {
        return name + "\n" + authors;
    }
    // KEEP METHODS END

}