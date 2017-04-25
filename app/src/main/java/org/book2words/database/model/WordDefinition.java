package org.book2words.database.model;

import org.book2words.translate.core.Definition;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dima on 24.04.17.
 */

public class WordDefinition implements Definition{

    private String text;
    private String transcription;
    private String pos;
    private String translate;

    public WordDefinition() {
    }

    public WordDefinition(String text, String transcription, String pos, String translate) {
        this.text = text;
        this.transcription = transcription;
        this.pos = pos;
        this.translate = translate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
