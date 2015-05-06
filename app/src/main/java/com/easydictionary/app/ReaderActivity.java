package com.easydictionary.app;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.*;
import java.nio.charset.Charset;

public class ReaderActivity extends Activity {

    private static final String JAVASCRIPT_INTERFACE = "INTERFACE";
    private static final String JAVASCRIPT_GET_BODY_INNER_TEXT = "javascript:window." + JAVASCRIPT_INTERFACE
            + ".processContent(document.getElementsByTagName('body')[0].innerText);";
    private static final String ENCODING = Charset.forName("utf-8").name();
    private static final String MIME_TYPE = "text/html; charset=UTF-8";
    private static final String TAG = ReaderActivity.class.getSimpleName();

    private WebView bookView;
    private TextView titleView;
    private BookReader reader;
    private String bookName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookView = (WebView) findViewById(android.R.id.text1);
        titleView = (TextView) findViewById(android.R.id.text2);
        WebSettings settings = bookView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName(ENCODING);
        bookView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl(JAVASCRIPT_GET_BODY_INNER_TEXT);
            }
        });

        findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reader.previous();
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reader.next();
            }
        });

        findViewById(R.id.tr_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextSplitService.start(ReaderActivity.this, bookName);
                bookView.addJavascriptInterface(new TextFetcher(ReaderActivity.this, reader), JAVASCRIPT_INTERFACE);
                bookView.reload();
                Log.d(TAG, "reading started");
            }
        });

        Uri path = getIntent().getData();

        try {

            // find InputStream for book

            InputStream epubInputStream = new FileInputStream(new File(path.getPath()));

            // Load Book from inputStream

            Book book = (new EpubReader()).readEpub(epubInputStream, ENCODING);
            bookName = book.getTitle();

            reader = new BookReader(book.getSpine(), new BookFetcher() {
                @Override
                public void onChapter(final String title, final String data) {
                    bookView.post(new Runnable() {
                        @Override
                        public void run() {
                            titleView.setText(title);
                            bookView.loadData(data, MIME_TYPE, ENCODING);
                        }
                    });
                }

                @Override
                public void onEnd() {
                    TextSplitService.stop(ReaderActivity.this, bookName);
                    bookView.post(new Runnable() {
                        @Override
                        public void run() {
                            bookView.removeJavascriptInterface(JAVASCRIPT_INTERFACE);
                        }
                    });
                    Log.d(TAG, "reading end");
                }
            });
            reader.start(57);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private static final class BookReader implements BookPager {
        private final Spine spine;
        private final BookFetcher fetcher;

        private int currentChapter = -1;

        private BookReader(Spine spine, BookFetcher fetcher) {
            this.spine = spine;
            this.fetcher = fetcher;
        }

        @Override
        public void next() {
            for (; currentChapter++ < spine.size(); ) {
                if (currentChapter < spine.size()) {
                    String text = getText();
                    String title = getTitle();
                    if (!TextUtils.isEmpty(text.trim())) {
                        fetcher.onChapter(title, text);
                        break;
                    }
                } else {
                    currentChapter = spine.size() - 1;
                    fetcher.onEnd();
                    break;
                }
            }
        }

        @Override
        public void previous() {
            for (; currentChapter-- >= 0; ) {
                if (currentChapter >= 0) {
                    String text = getText();
                    String title = getTitle();
                    if (!TextUtils.isEmpty(text.trim())) {
                        fetcher.onChapter(title, text);
                        break;
                    }
                } else {
                    currentChapter = 0;
                    fetcher.onEnd();
                    break;
                }
            }
        }

        @Override
        public void start(int offset) {
            currentChapter = offset;
            fetcher.onChapter(getTitle(), getText());
        }

        private String getText() {
            Resource resource = spine.getResource(currentChapter);
            StringBuilder string = new StringBuilder();
            try {
                InputStream is = resource.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
                try {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        string.append(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return string.toString();
        }

        @Override
        public String getTitle() {
            Resource resource = spine.getResource(currentChapter);
            String title = null;
            if (resource != null) {
                title = resource.getTitle();
            }
            if (TextUtils.isEmpty(title)) {
                title = String.format("Chunk %d", currentChapter + 1);
            }
            return title;
        }
    }

    private static final class TextFetcher {
        private final Context context;
        private final BookPager pager;

        private TextFetcher(Context context, BookPager pager) {
            this.context = context;
            this.pager = pager;

        }

        @JavascriptInterface
        public void processContent(String aContent) {
            Log.d(TAG, "loaded " + pager.getTitle());
            if (!TextUtils.isEmpty(aContent)) {
                TextSplitService.split(context, pager.getTitle(), aContent);
            }
            pager.next();
        }
    }

    public interface BookPager {
        String getTitle();

        void next();

        void previous();

        void start(int offset);

    }

    public interface BookFetcher {
        void onChapter(String title, String data);

        void onEnd();
    }

}
