package com.example.cchat;

public class User {
    private String name;
    private String email;
    private String id;
    private String imageUri;

    public User() {
    }



    public User(String name, String email, String id, String imageUri) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.imageUri=imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
