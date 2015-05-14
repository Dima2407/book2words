package com.easydictionary.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.book2words.dao.LibraryDictionary;
import org.book2words.screens.DictionaryFragment;


public class DictionaryActivity extends Activity {

    public static final String EXTRA_DICTIONARY = "_dictionary";

    private static final String FRAGMENT_TAG = "fragment_100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LibraryDictionary data = getIntent().getParcelableExtra(EXTRA_DICTIONARY);

        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = DictionaryFragment.Companion.create(data);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }
}
