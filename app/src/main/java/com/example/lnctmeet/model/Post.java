package com.example.lnctmeet.model;

import java.util.Date;

public class Post {
    private String TAG,WEB_URL,DESCRIPTION,AUTHOR,VISIBLE_TO,IMAGE_URI,SAVED_IMAGE,UPLOADING_IMAGEURI,POST_ID;
    Date DATE_CREATION;

    public String getWEB_URL() {
        return WEB_URL;
    }

    public void setWEB_URL(String WEB_URL) {
        this.WEB_URL = WEB_URL;
    }

    public String getIMAGE_URI() {
        return IMAGE_URI;
    }

    public String getUPLOADING_IMAGEURI() {
        return UPLOADING_IMAGEURI;
    }

    public String getPOST_ID() {
        return POST_ID;
    }

    public void setPOST_ID(String POST_ID) {
        this.POST_ID = POST_ID;
    }

    public void setUPLOADING_IMAGEURI(String UPLOADING_IMAGEURI) {
        this.UPLOADING_IMAGEURI = UPLOADING_IMAGEURI;
    }

    public String getSAVED_IMAGE() {
        return SAVED_IMAGE;
    }

    public void setSAVED_IMAGE(String SAVED_IMAGE) {
        this.SAVED_IMAGE = SAVED_IMAGE;
    }

    public void setIMAGE_URI(String IMAGE_URI) {
        this.IMAGE_URI = IMAGE_URI;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    public Date getDATE_CREATION() {
        return DATE_CREATION;
    }

    public void setDATE_CREATION(Date DATE_CREATION) {
        this.DATE_CREATION = DATE_CREATION;
    }

    public String getVISIBLE_TO() {
        return VISIBLE_TO;
    }

    public void setVISIBLE_TO(String VISIBLE_TO) {
        this.VISIBLE_TO = VISIBLE_TO;
    }
}