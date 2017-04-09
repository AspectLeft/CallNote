package com.example.administrator.callnote;

/**
 * Created by Administrator on 2016/9/10.
 */
public class Keyword {
    private String name;
    private int imageId;
    public String getName()
    {
        return name;
    }
    public int getImageId()
    {
        return imageId;
    }

    public Keyword(String name, int imageId)
    {
        this.name = name;
        this.imageId = imageId;
    }
}
