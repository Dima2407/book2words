package com.easydictionary.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import org.book2dictionary.core.Dictionary;
import org.book2dictionary.core.Provider;
import org.book2dictionary.core.book.BookDictionary;
import org.models.Chapter;
import org.models.Paragraph;
import org.models.Patterns;
import org.models.TextSplitter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextSplitService extends Service {

    private static final String ACTION_PARSE = "com.easydictionary.app.action.PARSE";
    private static final String ACTION_END = "com.easydictionary.app.action.END";
    private static final String ACTION_BEGIN = "com.easydictionary.app.action.BEGIN";

    private static final String ACTION_ORGANIZE_DICTIONARY = "com.easydictionary.app.action.ORGANIZE_DICTIONARY";

    private static final String EXTRA_KEY = "_key";
    private static final String EXTRA_TEXT = "_text";
    private static final String EXTRA_BOOK = "_book";
    private static final String TAG = TextSplitService.class.getSimpleName();

    private HandlerThread handlerThread;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread(TextSplitService.class.getSimpleName());
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    public static void clearDictionary(Context context) {
        Intent intent = new Intent(context, TextSplitService.class);
        intent.setAction(ACTION_ORGANIZE_DICTIONARY);
        context.startService(intent);
    }

    public static void split(Context context, String key, String text) {
        Intent intent = new Intent(context, TextSplitService.class);
        intent.setAction(ACTION_PARSE);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_TEXT, text);
        context.startService(intent);
    }

    public static void stop(Context context, String name) {
        Intent intent = new Intent(context, TextSplitService.class);
        intent.setAction(ACTION_END);
        intent.putExtra(EXTRA_BOOK, name);
        context.startService(intent);
    }

    public static void start(Context context, String name) {
        Intent intent = new Intent(context, TextSplitService.class);
        intent.setAction(ACTION_BEGIN);
        intent.putExtra(EXTRA_BOOK, name);
        context.startService(intent);
    }

    public final Map<String, Map<String, Integer>> words = new LinkedHashMap<String, Map<String, Integer>>();
    private final TextSplitter textSplitter = new TextSplitter();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSE.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final String text = intent.getStringExtra(EXTRA_TEXT);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        splitText(key, text);
                    }
                });

            } else if (ACTION_BEGIN.equals(action)) {
                final String name = intent.getStringExtra(EXTRA_BOOK);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "book start");
                        textSplitter.release();
                    }
                });
            } else if (ACTION_END.equals(action)) {
                final String name = intent.getStringExtra(EXTRA_BOOK);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "book end");
                        filterText(name);
                    }
                });
            } else if (ACTION_ORGANIZE_DICTIONARY.equals(action)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        clearKnownWords();
                    }
                });
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void splitText(String key, String input_text) {
        textSplitter.findCapital(input_text);
        textSplitter.split(key, input_text);

        /*String[] text = input_text.split(RegexDictionary.words("[^\\w+'`’]"));
        Map<String, Integer> words = new LinkedHashMap<String, Integer>();
        for (String s : text) {
            String word = s.toLowerCase().trim();
            if(word.length() <= 2){
                continue;
            }
            if (word.startsWith("’") || word.endsWith("’") ||
                    word.startsWith("`") || word.endsWith("`") ||
                    skipWords.contains(word.toLowerCase()) || duplicatesCharacters.matcher(s).matches()) {
                Log.d(TAG, String.format("SKIP - %S --- %s", key, s));
                continue;
            }
            Integer count = words.get(word);
            words.put(word, count == null ? 1 : ++count);
        }
        this.words.put(key, words);
        Log.d(TAG, String.format("%s --- %d / %d", key, words.size(), text.length));
        Log.d(TAG, String.format("%s --- %.2f", key, (float) words.size() / text.length));*/
    }

    private void filterText(String name) {
        Dictionary dictionaryProvider = Dictionary.OBJECT$.openUserDictionary(Provider.File, Configs.getBooksDirectory());
        dictionaryProvider.prepare(false);

        Set<String> words = new TreeSet<String>();

        Pattern pattern = Pattern.compile(RegexDictionary.words(null));

        for (String w : dictionaryProvider) {
            Matcher matcher = pattern.matcher(w);
            if (!matcher.matches()) {
                words.add(w);
            }
        }

        dictionaryProvider.release();

        textSplitter.clearCapital();
        textSplitter.clearWithApostrophe();
        textSplitter.clearWidelyUsed(getResources().getStringArray(R.array.widely_worlds));
        textSplitter.clearWithDuplicates();
        textSplitter.clearWords(words);
        Log.d(TAG, "" + textSplitter.getChapters());

        BookDictionary provider = Dictionary.OBJECT$.<BookDictionary>createBookDictionary(Provider.ExelFile, Configs.getBooksDirectory(), name);

        provider.prepare(true);
        int count = 0;
        for (Chapter chapter : textSplitter.getChapters()) {
            List<Paragraph> map = chapter.getParagraphs();
            if (chapter.isEmpty()) {
                continue;
            }
            provider.addChapter(chapter.getKey());
            for(Paragraph paragraph : map){
                for (String w : paragraph.getWords()) {
                    provider.add(w);
                    count++;
                }
                provider.add("");
            }

        }
        Log.d(TAG, "end " + count);
        provider.release();
    }

    private void debugAll() {
        Set<String> keys = this.words.keySet();
        Map<String, Integer> all = new LinkedHashMap<String, Integer>();

        for (String key : keys) {
            Map<String, Integer> map = this.words.get(key);
            Set<String> wordsInChapter = map.keySet();
            if (wordsInChapter.isEmpty()) {
                continue;
            }
            for (String word : wordsInChapter) {
                Integer count = all.get(word);
                all.put(word, count == null ? map.get(word) : count + map.get(word));
            }

        }
        BufferedWriter manyTimeWriter = null;
        BufferedWriter oneTimeWriter = null;
        try {
            manyTimeWriter = new BufferedWriter(new FileWriter("/sdcard/Books/all.txt"));
            oneTimeWriter = new BufferedWriter(new FileWriter("/sdcard/Books/one.txt"));

            int manyCount = 0;
            int oneCount = 0;
            for (String key : all.keySet()) {
                if (all.get(key) > 1) {
                    manyTimeWriter.write(key);
                    manyTimeWriter.newLine();
                    manyTimeWriter.flush();
                    manyCount++;
                } else {
                    oneTimeWriter.write(key);
                    oneTimeWriter.newLine();
                    oneTimeWriter.flush();
                    oneCount++;
                }
            }

            Log.d(TAG, "many " + manyCount + ", one " + oneCount);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (manyTimeWriter != null) {
                try {
                    manyTimeWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oneTimeWriter != null) {
                try {
                    oneTimeWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "end " + all.size());
    }

    private void clearKnownWords() {
        Dictionary dictionaryProvider = Dictionary.OBJECT$.openUserDictionary(Provider.File, Configs.getBooksDirectory());
        dictionaryProvider.prepare(false);

        Set<String> words = new TreeSet<String>();

        List<String> old = Arrays.asList(getResources().getStringArray(R.array.widely_worlds));

        for (String w : dictionaryProvider) {
            Matcher matcher = Patterns.WITH_APOSTROPHE.matcher(w);
            Matcher duplicates = Patterns.DUPLICATES.matcher(w);
            if (!matcher.matches() && !duplicates.matches() && !old.contains(w)) {
                words.add(w);
            }
        }

        dictionaryProvider.release();

        dictionaryProvider.prepare(true);
        for (String w : words) {
            dictionaryProvider.add(w);
        }
        dictionaryProvider.release();
        Log.d(TAG, "clearKnownWords");

    }

    @Override
    public void onDestroy() {
        handlerThread.quit();
        super.onDestroy();
    }

    private static class RegexDictionary {
        private static final String[] words = {
                "\\w*\\d+\\w*",
                "\\w{1,2}",
                "ha(s|ve)(n’t)?",
                "(was|were)(n’t)?",
                "(are|is)(n’t)?",
                "(do|does|did)(n’t)?",
                "(will)(n’t)?",
                "(\\w)+((’ve)|(’re)|(’ll)|(’s)|(’d)|(’t))"
        };

        public static String words(String original) {
            StringBuilder builder = new StringBuilder();
            builder.append("(?i)(");
            if (!TextUtils.isEmpty(original)) {
                builder.append("(").append(original).append(")|");
            }
            builder.append("(\\b(");
            for (int i = 0; i < words.length; i++) {
                builder.append("(").append(words[i]).append(")");
                if (i != words.length - 1) {
                    builder.append("|");
                }
            }
            builder.append(")\\b)");
            builder.append(")");
            return builder.toString();
        }

        public static String duplicateCharacters() {
            String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < alphabet.length; i++) {
                String character = alphabet[i];
                builder.append("([\\w\\d]*").append(character.toLowerCase()).append("{3,}[\\w\\d]*)");
                builder.append("|");
                builder.append("([\\w\\d]*").append(character.toUpperCase()).append("{3,}[\\w\\d]*)");
                if (i != alphabet.length - 1) {
                    builder.append("|");
                }
            }
            return builder.toString();
        }
    }

}
