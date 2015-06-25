package org.book2words;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import org.book2words.screens.DictionarySettingsFragment;
import org.book2words.screens.LibraryListFragment;


public class MainActivity extends FragmentActivity {

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final Context context;

        public ScreenSlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            this.context = activity;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new DictionarySettingsFragment();
            } else {
                return LibraryListFragment.Companion.create();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return context.getString(R.string.settings);
            } else {
                return context.getString(R.string.library);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
