package org.book2words.database.model;

/**
 * Created by user on 19.04.2017.
 */

public class Part {

    private long id;
    private long bookId;
    private int partitionNumber;
    private int paragraphNumber;
    private int amountOfWords;
    private int amountOfSymbols;
    private String text;

    public Part() {
    }

    public Part(int id) {
        this.id = id;
    }

    public Part(int id, Long bookId, int partitionNumber, int amountOfWords, int amountOfSymbols, String text) {
        this.id = id;
        this.bookId = bookId;
        this.partitionNumber = partitionNumber;
        this.amountOfWords = amountOfWords;
        this.amountOfSymbols = amountOfSymbols;
        this.text = text;
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

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public void setPartitionNumber(int partitionNumber) {
        this.partitionNumber = partitionNumber;
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
