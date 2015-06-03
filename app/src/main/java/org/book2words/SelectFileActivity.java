package org.book2words;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.book2words.screens.SelectBookDialogFragment;

public class SelectFileActivity extends Activity {
    public static final String EXTRA_EXTENSION = "_extension";
    public static final String EXTRA_OUTPUT = "_output";
    private static final String FRAGMENT_TAG = "fragment:select_folder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            String extension = getIntent().getStringExtra(EXTRA_EXTENSION);
            fragment = SelectBookDialogFragment.Companion.create(extension);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }
}
