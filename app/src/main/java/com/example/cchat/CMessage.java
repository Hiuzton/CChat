package com.example.cchat;

public class CMessage {

    private String text;
    private String name;
    private String sender;
    private String recipient;
    private String imageUrl;
    private boolean mineMessage;

    public CMessage(){

    }

    public CMessage(String text, String name, String sender,
                    String recipient, String imageUrl, boolean mineMessage) {
        this.text = text;
        this.name = name;
        this.sender = sender;
        this.recipient = recipient;
        this.imageUrl = imageUrl;
        this.mineMessage = mineMessage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isMineMessage() {
        return mineMessage;
    }

    public void setMineMessage(boolean mineMessage) {
        this.mineMessage = mineMessage;
    }
}
