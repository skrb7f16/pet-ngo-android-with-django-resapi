package com.skrb7f16.petngo.Models;

public class Donations {

    private int amount;
    private String doneOn;
    private int id;

    public Donations(int amount, String doneOn, int id) {
        this.amount = amount;
        this.doneOn = doneOn;
        this.id = id;
    }

    public Donations(int amount, String doneOn) {
        this.amount = amount;
        this.doneOn = doneOn;
    }

    public Donations() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDoneOn() {
        return doneOn;
    }

    public void setDoneOn(String doneOn) {
        this.doneOn = doneOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
