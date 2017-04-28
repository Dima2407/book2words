package org.book2words;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.book2words.core.Logger;
import org.book2words.screens.DictionarySettingsFragment;
import org.book2words.screens.LibraryListFragment;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String tag = LibraryListFragment.Companion.getTAG();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null){
            fragment = LibraryListFragment.Companion.create();
        }
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment , tag)
                .commit();
    }
}
