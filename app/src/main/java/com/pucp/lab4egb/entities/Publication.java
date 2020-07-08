package com.pucp.lab4egb.entities;

import java.util.ArrayList;

public class Publication {

    private String publicationId;
    private String userName;
    private String image;
    private String date;
    private String description;
    private String cant_comments;


    public String getCant_comments() {
        return cant_comments;
    }

    public void setCant_comments(String cant_comments) {
        this.cant_comments = cant_comments;
    }


    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
