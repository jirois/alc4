package com.example.travelmantic;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String descrp;
    private String price;
    private String imageUri;
    private String pictureName;


    public TravelDeal(){}

    public TravelDeal(String title, String descrp, String price, String imageUri) {
        this.id = id;
        this.title = title;
        this.descrp = descrp;
        this.price = price;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescrp() {
        return descrp;
    }

    public void setDescrp(String descrp) {
        this.descrp = descrp;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setImageName(String pictureName) {
        this.pictureName = pictureName;
    }
}
