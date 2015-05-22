package org.book2words.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import org.book2words.Configs;
import org.book2dictionary.core.*;
import org.book2dictionary.core.book.BookDictionary;

import java.util.ArrayList;
import java.util.List;


public class TranslateService extends Service {

    private static final String ACTION_TRANSLATE = "com.easydictionary.app.action.TRANSLATE";
    private static final String ACTION_CLEAR = "com.easydictionary.app.action.CLEAR";
    private static final String ACTION_CLEAR_CHAPTER = "com.easydictionary.app.action.CLEAR_CHAPTER";

    private static final String EXTRA_KEY = "_key";
    private static final String TAG = TranslateService.class.getSimpleName();
    private static final String EXTRA_LIST = "_list";
    private static final String EXTRA_INDEX = "_index";

    private HandlerThread handlerThread;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread(TranslateService.class.getSimpleName());
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    public static void translate(Context context, String key) {
        Intent intent = new Intent(context, TranslateService.class);
        intent.setAction(ACTION_TRANSLATE);
        intent.putExtra(EXTRA_KEY, key);
        context.startService(intent);
    }

    public static void clear(Context context, String key, int chapter) {
        Intent intent = new Intent(context, TranslateService.class);
        intent.setAction(ACTION_CLEAR);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_INDEX, chapter);
        context.startService(intent);
    }

    public static void clear(Context context, String key, ArrayList<String> list, int chapter) {
        Intent intent = new Intent(context, TranslateService.class);
        intent.setAction(ACTION_CLEAR_CHAPTER);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_INDEX, chapter);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TRANSLATE.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        translateText(key);
                    }
                });

            } else if (ACTION_CLEAR.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final int index = intent.getIntExtra(EXTRA_INDEX, 0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        clearText(key, index);
                    }
                });
            }else if (ACTION_CLEAR_CHAPTER.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final ArrayList<String> values = intent.getStringArrayListExtra(EXTRA_LIST);
                final int index = intent.getIntExtra(EXTRA_INDEX, 0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        clearText(key, values, index);
                    }
                });
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void translateText(String key) {
        /*final BookDictionary provider = Dictionary.OBJECT$.<BookDictionary>openBookDictionary(Provider.ExelFile, Configs.getBooksDirectory(), key);
        try {

            provider.prepare(true);

            TranslateProvider translateProvider = TranslateProviderFactory.create(TranslateProvider.Provider.YANDEX, "en", "ru");
            final int[] i = {0};
            while (provider.moveToChapter(i[0]) && i[0] < 3) {
                final List<String> values = new ArrayList<String>();

                for (String word : provider) {
                    values.add(word);
                }

                final AtomicInteger index = new AtomicInteger(0);
                final Object o = "";
                for (String w : values) {
                    translateProvider.translate(w, new TranslateHandler() {
                        @Override
                        public void onTranslate(String word, DictionaryResult result) {
                            String mean = result.takeResult();
                            provider.add(TextUtils.isEmpty(mean) ? word : mean);
                            Log.d(TAG, result.toString());

                            if (values.size() == index.incrementAndGet()) {
                                Log.d(TAG, "release " + i[0]);
                                synchronized (o) {
                                    o.notify();
                                }
                            }

                        }
                    });

                }
                synchronized (o) {
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                i[0]++;

            }
            Log.d(TAG, "release");
        } finally {
            Log.d(TAG, "release");
            provider.release();
        }*/

    }


    private void clearText(String key, int index) {
        Log.d(TAG, "clearText - started");
        Dictionary userDictionary = Dictionary.OBJECT$.openUserDictionary(Provider.File, Configs.getBooksDirectory());
        userDictionary.prepare(false);

        List<String> values = new ArrayList<String>();
        for (String w : userDictionary) {

            values.add(w);
        }
        userDictionary.release();

        BookDictionary bookDictionary = Dictionary.OBJECT$.<BookDictionary>openBookDictionary(Configs.getBookDictionaryProvider(), Configs.getBooksDirectory(), key);
        bookDictionary.prepare(true);
        for (int i = index; bookDictionary.moveToChapter(i); i++) {
            for (String w : values) {
                if (bookDictionary.remove(w)) {
                    Log.d(TAG, "clearText - removed : " + w);
                }
            }
            Log.d(TAG, "clearText - chunk : " + i);
        }

        Log.d(TAG, "clearText - finished");

        bookDictionary.release();
    }


    private void clearText(String key, List<String> values, int index) {
        Log.d(TAG, "clearText - started");
        Dictionary userDictionary = Dictionary.OBJECT$.openUserDictionary(Provider.File, Configs.getBooksDirectory());
        userDictionary.prepare(true);

        for (String word : values) {
            Log.d(TAG, "clearText - added : " + word);
            userDictionary.add(word);
        }
        userDictionary.release();

        BookDictionary bookDictionary = Dictionary.OBJECT$.<BookDictionary>openBookDictionary(Configs.getBookDictionaryProvider(), Configs.getBooksDirectory(), key);
        bookDictionary.prepare(true);
        for (int i = index; bookDictionary.moveToChapter(i); i++) {
            for (String word : values) {
                if (bookDictionary.remove(word)) {
                    Log.d(TAG, "clearText - removed : " + word);
                }
            }
            Log.d(TAG, "clearText - chunk : " + i);
        }

        Log.d(TAG, "clearText - finished");

        bookDictionary.release();
    }

    @Override
    public void onDestroy() {
        handlerThread.quit();
        super.onDestroy();
    }


}
