package com.example.michael.stakswipe;

/**
 * Created by miche_000 on 7/25/2017.
 * gives the information for a gif posting on reddit
 */

public class Gif {
    private Source source;

    private Resolutions[] resolutions;

    public Source getSource ()
    {
        return source;
    }

    public void setSource (Source source)
    {
        this.source = source;
    }

    public Resolutions[] getResolutions ()
    {
        return resolutions;
    }

    public void setResolutions (Resolutions[] resolutions)
    {
        this.resolutions = resolutions;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [source = "+source+", resolutions = "+resolutions+"]";
    }
}
