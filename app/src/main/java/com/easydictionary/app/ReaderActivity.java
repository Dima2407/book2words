package com.easydictionary.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.book2words.dao.LibraryBook;
import org.book2words.screens.BookReadFragment;
import org.book2words.screens.BookSplitFragment;


public class ReaderActivity extends Activity {

    public static final String EXTRA_BOOK = "_book";
    private static final String FRAGMENT_TAG = "_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LibraryBook data = getIntent().getParcelableExtra(EXTRA_BOOK);
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = BookReadFragment.Companion.create(data);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }
}
