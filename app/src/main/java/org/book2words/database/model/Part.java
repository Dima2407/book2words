package org.book2words.database.model;

/**
 * Created by user on 19.04.2017.
 */

public class Part {

    private int id;
    private Long bookId;
    private int partitionNumber;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
