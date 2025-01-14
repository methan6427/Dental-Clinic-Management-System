package com.example.phase4_1220813_122856_1210475;
public class Paying {
    private int paymentId;
    private String type;
    private double amountPaid;
    private String currency;
    private int invoiceId;
    private String paymentDate;
    private int employeeSsn; 

    public Paying(int paymentId, String type, double amountPaid, String currency, int invoiceId, String paymentDate, int employeeSsn) {
        this.paymentId = paymentId;
        this.type = type;
        this.amountPaid = amountPaid;
        this.currency = currency;
        this.invoiceId = invoiceId;
        this.paymentDate = paymentDate;
        this.employeeSsn = employeeSsn;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getEmployeeSsn() {
        return employeeSsn;
    }

    public void setEmployeeSsn(int employeeSsn) {
        this.employeeSsn = employeeSsn;
    }
}