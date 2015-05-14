package com.easydictionary.app;

import android.app.Activity;
import android.os.Bundle;
import org.book2words.dao.LibraryBook;


public class ReaderActivity extends Activity {

    public static final String EXTRA_BOOK = "_book";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LibraryBook data = getIntent().getParcelableExtra(EXTRA_BOOK);
    }
}
