package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.book2words.database.Schema.KnownWordTable;
import org.book2words.database.models.KnownWord;
import org.book2words.models.LibraryDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UsedWordsDao {

    private SQLiteDatabase sqLiteDatabase;

    UsedWordsDao(SQLiteDatabase database) {
        sqLiteDatabase = database;
    }

    public void save(KnownWord word) {
        ContentValues cv = new ContentValues();
        cv.put(KnownWordTable.COLUMN_WORD, word.getWord());
        sqLiteDatabase.insertWithOnConflict(KnownWordTable.TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Set<String> getAll() {
        Set<String> knownWords = new TreeSet<>();
        Cursor cursor = sqLiteDatabase.query(KnownWordTable.TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnWordIndex = cursor.getColumnIndex(KnownWordTable.COLUMN_WORD);
            do {
                knownWords.add(cursor.getString(columnWordIndex));
            } while (cursor.moveToNext());
        }
        return knownWords;
    }

    public void delete(String word) {
        sqLiteDatabase.delete(KnownWordTable.TABLE, KnownWordTable.COLUMN_WORD + "=?", new String[]{word});
    }

    public List<LibraryDictionary> getDictionaries() {
        List<LibraryDictionary> knownWords = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(KnownWordTable.TABLE, new String[]{"SUBSTR(" + KnownWordTable.COLUMN_WORD + ",1,1)", "COUNT(" + KnownWordTable.COLUMN_WORD + ")"}, null, null, "SUBSTR(" + KnownWordTable.COLUMN_WORD + ",1,1)", null, null);
        if (cursor.moveToFirst()) {
            do {
                knownWords.add(new LibraryDictionary(cursor.getString(0), cursor.getInt(1)));
            } while (cursor.moveToNext());
        }
        return knownWords;
    }

    @NotNull
    public List<String> findByStartCharacter(String name) {
        Set<String> knownWords = new TreeSet<>();
        Cursor cursor = sqLiteDatabase.query(KnownWordTable.TABLE, null, KnownWordTable.COLUMN_WORD + " LIKE '" + name + "%'", null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnWordIndex = cursor.getColumnIndex(KnownWordTable.COLUMN_WORD);
            do {
                knownWords.add(cursor.getString(columnWordIndex));
            } while (cursor.moveToNext());
        }
        return new ArrayList<>(knownWords);
    }
}
