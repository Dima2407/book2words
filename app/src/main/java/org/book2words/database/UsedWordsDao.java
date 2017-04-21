package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.book2words.database.model.UsedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaa-dev on 21.04.17.
 */

public class UsedWordsDao {

    private static final String TABLE_NAME = "USED_WORDS";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_SPEECH_PART = "speech_part";
    private static final String COLUMN_TRANSCRIPTION = "transcription";
    private static final String COLUMN_TRANSLATE = "transcription";
    private static final String SEPARATOR = ";";

    private SQLiteDatabase sqLiteDatabase;

    UsedWordsDao(SQLiteDatabase database) {
        sqLiteDatabase = database;
    }

    public UsedWord getUsedWord(String word){
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_WORD + "=?", new String[]{String.valueOf(word)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdindex = cursor.getColumnIndex(COLUMN_ID);
            final int columnWordIndex = cursor.getColumnIndex(COLUMN_WORD);
            final int columnSpeechPartIndex = cursor.getColumnIndex(COLUMN_SPEECH_PART);
            final int columnTranscriptionIndex = cursor.getColumnIndex(COLUMN_TRANSCRIPTION);
            final int columnTranslateIndex = cursor.getColumnIndex(COLUMN_TRANSLATE);

            UsedWord usedWord = new UsedWord(cursor.getString(columnWordIndex));
            usedWord.setId(cursor.getInt(columnIdindex));
            usedWord.setPartOfSpeech(cursor.getString(columnSpeechPartIndex));
            usedWord.setTranscription(cursor.getString(columnTranscriptionIndex));
            String[] translates = cursor.getString(columnTranslateIndex).split(SEPARATOR);
            usedWord.setTranslate(translates);
            return usedWord;
        }
        return null;
    }

    public void addUsedWord(UsedWord word){
        ContentValues cv = new ContentValues();
        if (word.getId() > 0)
            cv.put(COLUMN_ID, word.getId());
        cv.put(COLUMN_WORD, word.getWord());
        cv.put(COLUMN_SPEECH_PART, word.getPartOfSpeech());
        cv.put(COLUMN_TRANSCRIPTION, word.getTranscription());
        StringBuilder sb = new StringBuilder();
        for (String translate : word.getTranslate()) {
            sb.append(translate).append(SEPARATOR);
        }
        cv.put(COLUMN_TRANSLATE, sb.toString());
        sqLiteDatabase.insert(TABLE_NAME, null, cv);
    }

    public List<UsedWord> getAll(){
        List<UsedWord> usedWords = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdindex = cursor.getColumnIndex(COLUMN_ID);
            final int columnWordIndex = cursor.getColumnIndex(COLUMN_WORD);
            final int columnSpeechPartIndex = cursor.getColumnIndex(COLUMN_SPEECH_PART);
            final int columnTranscriptionIndex = cursor.getColumnIndex(COLUMN_TRANSCRIPTION);
            final int columnTranslateIndex = cursor.getColumnIndex(COLUMN_TRANSLATE);
            do {
                UsedWord usedWord = new UsedWord(cursor.getString(columnWordIndex));
                usedWord.setId(cursor.getInt(columnIdindex));
                usedWord.setPartOfSpeech(cursor.getString(columnSpeechPartIndex));
                usedWord.setTranscription(cursor.getString(columnTranscriptionIndex));
                String[] translates = cursor.getString(columnTranslateIndex).split(SEPARATOR);
                usedWord.setTranslate(translates);
                usedWords.add(usedWord);
            } while (cursor.moveToNext());
        }
        return usedWords;
    }

    public void delete(String word){
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_WORD + "=?", new String[]{String.valueOf(word)});
    }

    public void update(UsedWord word){
        delete(word.getWord());
        addUsedWord(word);
    }

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_WORD + "' TEXT NOT NULL, " +
                "'" + COLUMN_SPEECH_PART + "' TEXT, '" + COLUMN_TRANSCRIPTION + "' TEXT, '" + COLUMN_TRANSLATE + "' TEXT );";
    }

}
