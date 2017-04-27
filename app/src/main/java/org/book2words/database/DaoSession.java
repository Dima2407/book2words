package org.book2words.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DaoSession {

    private static final String DB_NAME = "book2word.db";
    private static final int DB_VERSION = 1;

    private final SQLiteOpenHelper dbHelper;

    private LibraryBookDao libraryBookDao;
    private WordsFoundDao wordsFoundDao;
    private PartsDao partsDao;
    private UsedWordsDao usedWordsDao;
    private DictionaryDao dictionaryDao;

    public DaoSession(Context context) {
        dbHelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(LibraryBookDao.obtainCreateInstancesQuery());
                db.execSQL(DictionaryDao.obtainCreateInstancesQuery());
                final List<String> setup = new Schema().setup();
                for (String query : setup){
                    db.execSQL(query);
                }

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }


    @NotNull
    public LibraryBookDao getLibraryBookDao() {
        if(libraryBookDao == null){
            libraryBookDao = new LibraryBookDao(dbHelper.getWritableDatabase());
        }
        return libraryBookDao;
    }

    @NotNull
    public WordsFoundDao getWordsFoundDao() {
        if(wordsFoundDao == null){
            wordsFoundDao = new WordsFoundDao(dbHelper.getWritableDatabase());
        }
        return wordsFoundDao;
    }

    public PartsDao getPartsDao() {
        if(partsDao == null){
            partsDao = new PartsDao(dbHelper.getWritableDatabase());
        }
        return partsDao;
    }

    public UsedWordsDao getUsedWordsDao() {
        if(usedWordsDao == null){
            usedWordsDao = new UsedWordsDao(dbHelper.getWritableDatabase());
        }
        return usedWordsDao;
    }

    public DictionaryDao getDictionaryDao() {
        if(dictionaryDao == null){
            dictionaryDao = new DictionaryDao(dbHelper.getWritableDatabase());
        }
        return dictionaryDao;
    }
}
