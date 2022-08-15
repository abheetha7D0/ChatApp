package model;

import java.io.Serializable;

public class Message implements Serializable {
    String name;
    String message;
    String image;

    public Message() {
    }

    public Message(String name, String message, String image) {
        this.name = name;
        this.message = message;
        this.image = image;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                '}';
    }



}
