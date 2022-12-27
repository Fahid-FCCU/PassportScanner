package com.example.passportscanner;

public class ScannedDataModel {
    private long timestamp;
    private String text;
    private String type;
    public static String TYPE_NFC = "NFC";
    public static String TYPE_OCR = "OCR";

    public ScannedDataModel(long timestamp, String text, String type) {
        this.timestamp = timestamp;
        this.text = text;
        this.type = type;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
