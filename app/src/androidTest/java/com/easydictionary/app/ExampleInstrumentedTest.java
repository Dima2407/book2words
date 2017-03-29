package com.easydictionary.app;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.book2words.services.LibraryService;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.book2words", appContext.getPackageName());
    }

    @Test
    public void prepareBook() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        final File directory = Environment.getExternalStorageDirectory();
        LibraryService.Companion.addBook(appContext, new File(directory,"Rage.epub"));
        SystemClock.sleep(5000);
    }
}
