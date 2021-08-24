package com.samsan.xcapeapplication.vo;

public class HintVO {
    private String key;
    private String message1;
    private String message2;

    public HintVO(String key, String message1, String message2) {
        this.key = key;
        this.message1 = message1;
        this.message2 = message2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }
}
