package com.example.budgeting_app;



public class Data {
    String date, details, category;
    int month;
    float amount;


    public Data(){


    }

    public Data(String date, String details, float amount, int month, String category) {

        this.date = date;
        this.details = details;
        this.category = category;
        this.amount = amount;
        this.month = month;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }


}
