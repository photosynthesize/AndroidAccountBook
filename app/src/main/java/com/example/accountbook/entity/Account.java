package com.example.accountbook.entity;

public class Account {
    private int id;
    private int type;
    private String tag;
    private String date;
    private double amount;
    private String detail;

    public Account() {
    }

    public Account(int id, int type, String tag, String date, double amount, String detail) {
        this.id = id;
        this.type = type;
        this.tag = tag;
        this.date = date;
        this.amount = amount;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
