package com.jadli.bookstore.level5.stats;

public class SalesReport extends BaseReport {

    private String note = "none";

    private final int year;

    public SalesReport(int year) {
        super("Sales " + year);
        this.year = year;
    }

    @Override
    protected void init() {
        this.note = "sales";
    }

    public String describe() {
        return render() + " / " + note + " / " + year;
    }
}
