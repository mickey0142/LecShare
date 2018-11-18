package model;

import java.io.Serializable;

public class User implements Serializable{
    private String username;
    private String aboutMe;
    private String email;
    private double averageScore;
    private int money;
    private String documentId;

    public User()
    {

    }

    public User(String username, String aboutMe, String email)
    {
        this.username = username;
        this.aboutMe = aboutMe;
        this.email = email;
        this.averageScore = 0;
        this.setMoney(0);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String studentNumber) {
        this.aboutMe = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int money)
    {
        this.money += money;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
