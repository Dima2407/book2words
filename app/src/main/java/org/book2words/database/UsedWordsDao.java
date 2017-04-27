package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.book2words.database.Schema.KnownWordTable;
import org.book2words.database.models.KnownWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UsedWordsDao {

    private SQLiteDatabase sqLiteDatabase;

    UsedWordsDao(SQLiteDatabase database) {
        sqLiteDatabase = database;
    }

    public KnownWord getUsedWord(String word) {
        Cursor cursor = sqLiteDatabase.query(KnownWordTable.TABLE, null, KnownWordTable.COLUMN_WORD + "=?", new String[]{String.valueOf(word)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(KnownWordTable._ID);
            final int columnWordIndex = cursor.getColumnIndex(KnownWordTable.COLUMN_WORD);

            KnownWord knownWord = new KnownWord(cursor.getInt(columnIdIndex), cursor.getString(columnWordIndex));
            return knownWord;
        }
        return null;
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
}
