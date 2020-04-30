package com.example.stcov;

public class UserHelperClass {
    String lastname,firstname,email,password,role;

    public UserHelperClass(){}

    public UserHelperClass(String firstname,String lastname,String email,String pwd){
        this.email = email;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = pwd;
        this.role = "user";
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
