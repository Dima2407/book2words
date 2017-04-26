package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.book2words.database.Schema.WordLocationTable;
import org.book2words.database.Schema.WordTable;
import org.book2words.models.book.Word;
import org.book2words.models.book.WordLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class WordsFoundDao {

    public static final String TAG = WordsFoundDao.class.getSimpleName();
    private static final int WORD_TRANSLATED = 1;
    private static final int WORD_NOT_TRANSLATED = 0;

    private SQLiteDatabase sqLiteDatabase;

    WordsFoundDao(SQLiteDatabase database) {

        sqLiteDatabase = database;
    }

    public void save(Iterable<Word> words) {
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues wordValues = new ContentValues();
            ContentValues wordLocationValues = new ContentValues();
            for (Word word : words) {

                wordValues.put(WordTable.COLUMN_VALUE, word.getValue());
                wordValues.put(WordTable.COLUMN_TRANSLATED, word.getTranslated() ? WORD_TRANSLATED : WORD_NOT_TRANSLATED);

                long id = sqLiteDatabase.insertWithOnConflict(WordTable.TABLE, null, wordValues, SQLiteDatabase.CONFLICT_IGNORE);
                for (WordLocation location : word.getLocations()) {
                    wordLocationValues.put(WordLocationTable.COLUMN_WORD_ID, id);
                    wordLocationValues.put(WordLocationTable.COLUMN_BOOK_ID, location.getBook());
                    wordLocationValues.put(WordLocationTable.COLUMN_PARAGRAPH_ID, location.getParagraph());
                    wordLocationValues.put(WordLocationTable.COLUMN_START, location.getStart());
                    wordLocationValues.put(WordLocationTable.COLUMN_END, location.getEnd());
                    sqLiteDatabase.insertWithOnConflict(WordLocationTable.TABLE, null, wordLocationValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void delete(@NotNull Word word) {
        sqLiteDatabase.beginTransaction();
        try {

            sqLiteDatabase.delete(WordTable.TABLE, WordTable._ID + "=" + word.getId(), null);
            sqLiteDatabase.delete(WordLocationTable.TABLE, WordLocationTable.COLUMN_WORD_ID + "=" + word.getId(), null);


            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @NotNull
    public Collection<Word> getWordsInBook(long book, List<Integer> paragraphs) {
        Map<String, Word> words = new TreeMap<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + WordLocationTable.TABLE + " left join " + WordTable.TABLE + " on " + WordTable._ID + "=" + WordLocationTable.COLUMN_WORD_ID + " where " + WordLocationTable.COLUMN_BOOK_ID + "=? AND " + WordLocationTable.COLUMN_PARAGRAPH_ID + " between ? and ?", new String[]{String.valueOf(book), String.valueOf(paragraphs.get(0)), String.valueOf(paragraphs.get(paragraphs.size() - 1))});
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(WordTable._ID);
            final int columnValueIndex = cursor.getColumnIndex(WordTable.COLUMN_VALUE);
            final int columnTranslatedIndex = cursor.getColumnIndex(WordTable.COLUMN_TRANSLATED);
            final int columnParagraphIndex = cursor.getColumnIndex(WordLocationTable.COLUMN_PARAGRAPH_ID);
            final int columnStartIndex = cursor.getColumnIndex(WordLocationTable.COLUMN_START);
            final int columnEndIndex = cursor.getColumnIndex(WordLocationTable.COLUMN_END);
            do {
                final String value = cursor.getString(columnValueIndex);
                Word word = words.get(value);
                if (word == null) {
                    word = new Word(value);
                    word.setId(cursor.getInt(columnIdIndex));
                    word.setTranslated(cursor.getInt(columnTranslatedIndex) == WORD_TRANSLATED);
                    words.put(value, word);
                }
                WordLocation location = new WordLocation(book, cursor.getInt(columnParagraphIndex),
                        cursor.getInt(columnStartIndex),
                        cursor.getInt(columnEndIndex));
                word.getLocations().add(location);
            } while (cursor.moveToNext());
        }
        return words.values();
    }
}
