package model;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable{
    private String username;
    private String aboutMe;
    private String email;
    private double averageScore;
    private int money;
    private String documentId;
    private String avatar;
    private String accessory;
    private HashMap<String, Boolean> inventory;

    public User()
    {

    }

    public User(String username, String aboutMe, String email, String avatar)
    {
        this.username = username;
        this.aboutMe = aboutMe;
        this.email = email;
        this.averageScore = 0;
        this.setMoney(0);
        this.inventory = new HashMap<>();
        getInventory().put("blue", false);
        getInventory().put("green", false);
        getInventory().put("grey", false);
        getInventory().put("red", false);
        getInventory().put(avatar, true);
        this.avatar = avatar;
        this.accessory = "";
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public HashMap<String, Boolean> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Boolean> inventory) {
        this.inventory = inventory;
    }
}
