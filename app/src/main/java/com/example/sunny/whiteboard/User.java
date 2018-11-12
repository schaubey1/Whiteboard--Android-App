package com.example.sunny.whiteboard;

/*
Need to extend Parceable class to allow passing through intent
 */

public class User {
    private String username;
    private String email;
    private String password;
    private String accountType;

    public User(String username, String email, String password, String accountType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountType() { return accountType; }
}
