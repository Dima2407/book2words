package org.book2words.database.model;

/**
 * Created by kaa-dev on 21.04.17.
 */

public class UsedWord {

    private int id;
    private String word;
    private String partOfSpeech;
    private String transcription;
    private String[] translate;

    public UsedWord(String word) {
        this.word = word;
    }

    public UsedWord(int id, String word, String partOfSpeech, String transcription, String[] translate) {
        this.id = id;
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.transcription = transcription;
        this.translate = translate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String[] getTranslate() {
        return translate;
    }

    public void setTranslate(String[] translate) {
        this.translate = translate;
    }
}
