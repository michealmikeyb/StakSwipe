package com.example.michael.stakswipe;

/**
 * Created by miche_000 on 7/25/2017.
 * gives the information for the image on a specific posting
 * in reddit
 */

public class Images {
    private String id;

    private Source source;

    private Variants variants;

    private Resolutions[] resolutions;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Source getSource ()
    {
        return source;
    }

    public void setSource (Source source)
    {
        this.source = source;
    }

    public Variants getVariants ()
    {
        return variants;
    }

    public void setVariants (Variants variants)
    {
        this.variants = variants;
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
        return "ClassPojo [id = "+id+", source = "+source+", variants = "+variants+", resolutions = "+resolutions+"]";
    }
}
