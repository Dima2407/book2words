package org.models

import org.book2dictionary
import org.book2dictionary.Logger
import java.util.ArrayList
import java.util.LinkedHashSet
import java.util.regex.Pattern

public class TextSplitter {

    val chapters = ArrayList<Chapter>();

    private val capitals = LinkedHashSet<String>();

    public fun findCapital(text: String) {
        val wordPattern = Patterns.CAPITAL_WORD;
        val matcher = wordPattern.matcher(text);
        var offset = 0;
        while (matcher.find(offset)) {
            offset = matcher.start(1);
            capitals.add(matcher.group(1));
        }
        Logger.debug("capitals " + capitals);

    }

    public fun split(key: String, text: String): Boolean {
        Logger.debug("split chapter " + key);
        val wordPattern = Patterns.WORD;

        val parts = text.split("\n+");
        var paragraphs = ArrayList<Paragraph>();
        for (paragraphText in parts) {
            val matcher = wordPattern.matcher(paragraphText);
            val words = ArrayList<String>();
            while (matcher.find()) {
                words.add(matcher.group(1));
            }
            Logger.debug("words " + words);
            paragraphs.add(Paragraph(words));
        }

        var chapter = Chapter(key, paragraphs);
        return chapters.add(chapter);
    }

    public fun clearCapital() {
        for (chapter in chapters) {
            for (p in chapter.paragraphs) {
                val iterator = p.words.listIterator()
                while (iterator.hasNext()) {
                    val word = iterator.next();
                    for (w in capitals) {
                        if (word.toLowerCase().equals(w.toLowerCase())) {
                            Logger.debug("remove " + word);
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }
    }


    public fun clearWidelyUsed(words: Array<String>) {

        for (chapter in chapters) {
            for (p in chapter.paragraphs) {
                val iterator = p.words.listIterator()
                while (iterator.hasNext()) {
                    val word = iterator.next();
                    if(words.contains(word.toLowerCase())){
                        Logger.debug("remove " + word);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public fun clearWithApostrophe() {
        val pattern = Patterns.WITH_APOSTROPHE;
        for (chapter in chapters) {
            for (p in chapter.paragraphs) {
                val iterator = p.words.listIterator()
                while (iterator.hasNext()) {
                    val word = iterator.next();
                    if (pattern.matcher(word).matches()) {
                        Logger.debug("remove " + word);
                        iterator.remove();
                    }
                }
            }
        }
    }


    public fun clearWithDuplicates() {
        val pattern = Patterns.DUPLICATES;
        for (chapter in chapters) {
            for (p in chapter.paragraphs) {
                val iterator = p.words.listIterator()
                while (iterator.hasNext()) {
                    val word = iterator.next();
                    if (pattern.matcher(word).matches()) {
                        Logger.debug("remove " + word);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public fun clearWords(words: Set<String>) {
        for (chapter in chapters) {
            for (p in chapter.paragraphs) {
                val iterator = p.words.listIterator()
                while (iterator.hasNext()) {
                    val word = iterator.next();
                    if(words.contains(word.toLowerCase())){
                        Logger.debug("remove " + word);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public fun release() {
        chapters.clear();
        capitals.clear();
    }
}