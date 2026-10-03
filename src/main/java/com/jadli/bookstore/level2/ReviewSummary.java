package com.jadli.bookstore.level2;

public class ReviewSummary {

    private final Long bookId;
    private final int count;
    private final double average;

    public ReviewSummary(Long bookId, int count, double average) {
        this.bookId = bookId;
        this.count = count;
        this.average = average;
    }

    public Long getBookId() {
        return bookId;
    }

    public int getCount() {
        return count;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReviewSummary)) {
            return false;
        }
        ReviewSummary that = (ReviewSummary) other;
        return bookId.equals(that.bookId) && count == that.count;
    }
}
