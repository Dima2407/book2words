package com.easydictionary.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import org.screens.LibraryListFragment;


public class MainActivity extends ListActivity {

    public static final String FRAGMENT_TAG = "fragment1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = LibraryListFragment.Companion.create(Configs.getBooksDirectory());

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }

}
