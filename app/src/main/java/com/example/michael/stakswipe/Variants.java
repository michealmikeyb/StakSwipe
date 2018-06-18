package com.example.michael.stakswipe;

/**
 * handles the variants of a certain listing
 * Created by miche_000 on 7/25/2017.
 */

public class Variants {
    private Mp4 mp4;

    private Gif gif;

    public Mp4 getMp4 ()
    {
        return mp4;
    }

    public void setMp4 (Mp4 mp4)
    {
        this.mp4 = mp4;
    }

    public Gif getGif ()
    {
        return gif;
    }

    public void setGif (Gif gif)
    {
        this.gif = gif;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [mp4 = "+mp4+", gif = "+gif+"]";
    }
}
