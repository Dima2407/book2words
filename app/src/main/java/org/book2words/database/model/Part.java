package org.book2words.database.model;


public class Part {

    private long id;
    private long bookId;
    private int paragraphNumber;
    private int amountOfWords;
    private int amountOfSymbols;
    private String text;

    public Part() {
    }

    public Part(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public int getAmountOfWords() {
        return amountOfWords;
    }

    public void setAmountOfWords(int amountOfWords) {
        this.amountOfWords = amountOfWords;
    }

    public int getAmountOfSymbols() {
        return amountOfSymbols;
    }

    public void setAmountOfSymbols(int amountOfSymbols) {
        this.amountOfSymbols = amountOfSymbols;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }
}
