package com.example.michael.stakswipe;

import java.time.Instant;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * a list of all the subreddits that the user has seen and holds each  place in each subreddit
 * Created by miche_000 on 7/26/2017.
 */

public class SubList {
    ArrayList<Sub> list;//the list to hold all the subreddits
    Calendar calendar;//keep track of when to reset the sublist
    final int TIME_TO_RESET = 12;//amount of time in hours it takes to reset;


    public SubList(){
        list = new ArrayList<Sub>();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, TIME_TO_RESET);//time to reset the sublist




    }

    /**
     * gets the id of the next listing of the subreddit given
     * @param s name of the subreddit to get the next listing from
     * @return the id of the next listing in the subreddit
     */
    public String getAfter(String s){
        boolean alreadyIn = false;
        for(Sub a: list){
            if(s!=null && a.after!=null && a.subreddit.equals(s)) {
                return a.after;
            }
        }
        return "notIn";
    }

    /**
     * sets the next id of a given subreddit
     * @param s the subreddit to change the next id
     * @param a the new id of the next listing
     */
    public void setAfter(String s, String a){
        boolean alreadyIn = false;
        for(Sub l: list){
            if(l.subreddit !=null && l.subreddit.equals(s)) {
                alreadyIn = true;
                l.setAfter(a);
                break;
            }
        }
        if(!alreadyIn)
            list.add(new Sub(s,a));
    }

    /**
     * adds a new subreddit to the list
     * @param s name of the new subreddit
     * @param a id of the next listing in the subreddit
     */
    public void add(String s, String a){
        boolean alreadyIn = false;
        for(Sub l: list){
            if(l.subreddit.equals(s))
                alreadyIn = true;
        }

        if(!alreadyIn)
            list.add(new Sub(s,a));
    }

    public String toString(){
        String s = "subs: ";
        for(Sub l: list){
            s+=l.subreddit+"\n";
        }
        return s;
    }

    public boolean checkReset(){
        Calendar now = Calendar.getInstance();
        if(now.after(calendar)){
            return true;
        }
        return false;
    }


}
