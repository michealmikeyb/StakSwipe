package com.example.michael.stakswipe;

/**
 * handles a piece of contents source
 * Created by miche_000 on 7/25/2017.
 */

public class Source {
    private String height;

    private String width;

    private String url;

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [height = "+height+", width = "+width+", url = "+url+"]";
    }
}
