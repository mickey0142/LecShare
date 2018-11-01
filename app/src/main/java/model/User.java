package model;

import java.io.Serializable;

public class User implements Serializable{
    private String username;
    private String studentNumber;
    private String email;

    public User()
    {

    }

    public User(String username, String studentNumber, String email)
    {
        this.username = username;
        this.studentNumber = studentNumber;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
