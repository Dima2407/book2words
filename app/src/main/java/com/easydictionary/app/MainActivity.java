package com.easydictionary.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.screens.DictionaryListFragment;
import org.screens.LibraryListFragment;


public class MainActivity extends Activity {

    public static final String FRAGMENT_TAG = "fragment1";

    public static final String FRAGMENT_MENU_TAG = "fragment2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadContentFragment(FRAGMENT_TAG);

        loadMenuFragment(FRAGMENT_MENU_TAG);
    }

    private void loadMenuFragment(String tag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new DictionaryListFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.dictionary_frame, fragment, tag);
            transaction.commit();
        }
    }

    private void loadContentFragment(String tag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = LibraryListFragment.Companion.create(Configs.getBooksDirectory());

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, tag);
            transaction.commit();
        }
    }

}
