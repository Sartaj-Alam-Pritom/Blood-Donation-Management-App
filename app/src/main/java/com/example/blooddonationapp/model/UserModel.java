package com.example.blooddonationapp.model;

public class UserModel {
    public String name, phone, email, password, DoB, address, catagory, gender;

    public UserModel() {
    }

    public UserModel(String name, String email, String phone, String catagory, String password, String gender, String DoB, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.catagory = catagory;
        this.password = password;
        this.gender = gender;
        this.DoB = DoB;
        this.address = address;
    }

}