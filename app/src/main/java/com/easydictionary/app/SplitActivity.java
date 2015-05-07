package com.easydictionary.app;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import org.book2words.dao.LibraryBook;
import org.screens.BookSplitFragment;

import java.io.*;
import java.nio.charset.Charset;

public class SplitActivity extends Activity {

    public static final String FRAGMENT_TAG = "fragment3";

    public static final String EXTRA_BOOK = "_book";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final LibraryBook data = getIntent().getParcelableExtra(EXTRA_BOOK);

        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = BookSplitFragment.Companion.create(data);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment, FRAGMENT_TAG);
            transaction.commit();
        }
    }

}
