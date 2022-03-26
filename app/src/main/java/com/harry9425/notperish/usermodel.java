package com.harry9425.notperish;

public class usermodel {
    String name,address,coordinates,email,dp,uid,phone;

    public usermodel(String uid) {
        this.uid = uid;
    }
    public usermodel() {}

    public usermodel(String name, String address, String coordinates, String email, String dp, String uid,String phone) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
        this.email = email;
        this.dp = dp;
        this.phone=phone;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
