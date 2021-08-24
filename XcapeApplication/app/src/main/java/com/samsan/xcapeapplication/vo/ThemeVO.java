package com.samsan.xcapeapplication.vo;


import com.google.gson.annotations.SerializedName;

public class ThemeVO {
    @SerializedName("themeCode")
    private String themeCode;
    @SerializedName("themeName")
    private String themeName;

    public ThemeVO(String themeCode, String themeName) {
        this.themeCode = themeCode;
        this.themeName = themeName;
    }

    public String getThemeCode() {
        return themeCode;
    }

    public void setThemeCode(String themeCode) {
        this.themeCode = themeCode;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
}
