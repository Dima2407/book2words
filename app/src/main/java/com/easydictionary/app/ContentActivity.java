package com.easydictionary.app;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.book2dictionary.core.reader.BookReader;

import java.util.List;


public class ContentActivity extends ListActivity {

    private BookReader bookReader;

    private String bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Uri data = getIntent().getData();

        ListView listView = getListView();

        View header = getLayoutInflater().inflate(R.layout.activity_content, null);
        listView.addHeaderView(header);

        ImageView cover = (ImageView) header.findViewById(android.R.id.icon1);
        TextView title = (TextView) header.findViewById(android.R.id.text1);
        header.findViewById(R.id.text_split).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, ReaderActivity.class);
                intent.setData(data);
                startActivity(intent);
            }
        });
        header.findViewById(R.id.text_dictionary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, MarkActivity.class);
                intent.putExtra(MarkActivity.EXTRA_NAME, bookTitle);
                startActivity(intent);
            }
        });
        header.findViewById(R.id.text_translate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateService.translate(ContentActivity.this, bookTitle);
            }
        });

        bookReader = BookReader.OBJECT$.create(data.getPath());
        bookReader.open();

        bookTitle = bookReader.getTitle();
        cover.setImageBitmap(bookReader.getCover());
        title.setText(bookReader.getDisplayTitle());
        List<String> items = bookReader.getTableOfContents();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);

        setListAdapter(adapter);

        bookReader.close();
    }

}
