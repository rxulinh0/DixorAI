package com.ai.dixorai;

import android.graphics.Color;

public class Tag {
    public String tag_name;
    public int color;

    public Tag(){
        this.tag_name = "raw";
        this.color = -1;
    }

    public Tag(String tag_name, int color) {
        this.tag_name = tag_name;
        this.color = color;
    }

    public Tag(String tag_name) {
        this.tag_name = tag_name;
        this.color = -1;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
