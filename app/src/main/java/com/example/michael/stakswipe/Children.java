package com.example.michael.stakswipe;

/**
 * Created by miche_000 on 7/25/2017.
 * children class for storing the child listings for a particular
 * reddit listing
 */

public class Children {
    private Data data;

    private String kind;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getKind ()
    {
        return kind;
    }

    public void setKind (String kind)
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", kind = "+kind+"]";
    }
}
