package com.example.michael.stakswipe;
/**
 * preview of a listing
 * Created by miche_000 on 7/25/2017.
 */

public class Preview {
    private String enabled;

    private Images[] images;

    public String getEnabled ()
    {
        return enabled;
    }

    public void setEnabled (String enabled)
    {
        this.enabled = enabled;
    }

    public Images[] getImages ()
    {
        return images;
    }

    public void setImages (Images[] images)
    {
        this.images = images;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [enabled = "+enabled+", images = "+images+"]";
    }
}
