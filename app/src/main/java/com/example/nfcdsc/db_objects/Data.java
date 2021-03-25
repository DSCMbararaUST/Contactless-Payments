package com.example.nfcdsc.db_objects;

public class Data {

    private String firstname;
    private String lastname;
    private String phone;
    private String balance;

    public Data(String firstname, String lastname, String phone, String balance) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.balance = balance;
    }

    public Data() {}

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getBalance() {
        return balance;
    }
}
