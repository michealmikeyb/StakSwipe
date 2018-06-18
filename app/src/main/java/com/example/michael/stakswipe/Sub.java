package com.example.michael.stakswipe;

/**
 * the current status of a subreddit holding the id of the next listing in each subreddit
 * Created by miche_000 on 7/26/2017.
 */

public class Sub {
    public String subreddit;//the name of the subreddit
    public String after;//the id of the next listing in the subreddit

    public Sub(String s, String a){
        subreddit = s;
        after = a;
    }

    public void setAfter(String a){
        after = a;
    }
}
