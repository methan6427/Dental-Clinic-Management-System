package com.example.phase4_1220813_122856_1210475;

public class Invoice {
    private int invoiceId;
    private double amount;
    private String date;
    private String status;
    private String dueDate;
    private int cid;

    public Invoice(int invoiceId, double amount, String date, String status, String dueDate, int cid) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.dueDate = dueDate;
        this.cid = cid;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}