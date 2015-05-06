package com.easydictionary.app;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.book2dictionary.core.Dictionary;
import org.book2dictionary.core.Provider;
import org.book2dictionary.core.book.BookDictionary;

import java.util.ArrayList;
import java.util.List;

public class MarkActivity extends ListActivity {

    private int activeChapter = 0;

    public static final String EXTRA_NAME = "_name";

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        name = getIntent().getStringExtra(EXTRA_NAME);
        loadChapter();

        findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeChapter--;
                saveChanges();
                loadChapter();
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeChapter++;
                saveChanges();
                loadChapter();
            }
        });
    }

    private void loadChapter() {
        BookDictionary reader = Dictionary.OBJECT$.<BookDictionary>openBookDictionary(Provider.ExelFile, Configs.getBooksDirectory(), name);
        reader.prepare(false);
        if(reader.moveToChapter(activeChapter)) {
            List<String> values = new ArrayList<String>();
            for (String word : reader) {
                values.add(word);
            }
            reader.release();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice, values);
            setListAdapter(adapter);
        }
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveChanges();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChanges() {
        ListView listView = getListView();
        SparseBooleanArray positions = listView.getCheckedItemPositions();

        final ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < listView.getCount(); i++) {

            if (positions.get(i)) {
                String word = listView.getItemAtPosition(i).toString().toLowerCase();
                items.add(word);
            }

        }
        if(!items.isEmpty()) {
            TranslateService.clear(this, name, items, activeChapter);
        }

    }
}
