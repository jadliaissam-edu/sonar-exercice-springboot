package com.jadli.bookstore.level5.stats;

public abstract class BaseReport {

    protected String title;

    protected String note;

    protected BaseReport(String title) {
        this.title = title;
        init();
    }

    protected void init() {
        this.note = "base";
    }

    public String render() {
        return title + " [" + note + "]";
    }
}
