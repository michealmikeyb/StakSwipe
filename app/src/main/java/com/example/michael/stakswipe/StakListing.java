package com.example.michael.stakswipe;

public class StakListing implements listingInterface{


    private String Title;
    private String Link;
    private String Author;
    private String Tag;
    private String Place;

    public StakListing(String title, String link, String author, String tag, String place){
        Title = title;
        Link = link;
        Author = author;
        Tag = tag;
        Place = place;
    }
    public String getTitle() {
        return Title;
    }

    public String getUrl() {
        return Link;
    }

    public String getAuthor() {
        return Author;
    }

    public String getTag() {
        return Tag;
    }

    public String getAfter() {
        return Place;
    }
    public String getDomain(){
        return "stak";
    }

}
