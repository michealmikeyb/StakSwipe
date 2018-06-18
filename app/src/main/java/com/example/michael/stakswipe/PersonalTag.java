package com.example.michael.stakswipe;

import java.util.ArrayList;

/**
 * content tag handled on device that shows how much a current subreddit is liked or disliked
 * Created by miche_000 on 7/24/2017.
 */

public class PersonalTag {
    public double rating;//rating on a scale of -100 to 100 showing how common the tag will come up, corresponds to the number of places in the taglist divided by 100
    public double LikeMultiplier;// how much a like or dislike will increase or decrease the rating
    public double DislikeMultiplier;
    public String name;//name of the subreddit/tag
    ArrayList<Integer> listNumbers;//the list of numbers that the tag occupies in the taglist

    /**
     * initializes a new tag with a name
     * @param n the name of the tag/subreddit
     */
    public PersonalTag(String n){
        name = n;
        rating = 0;
        LikeMultiplier = 0.5;
        DislikeMultiplier = 0.5;
        listNumbers = new ArrayList<Integer>();

    }

    public boolean isNegative(){
        return rating<0;
    }

    /**
     * handles the first time a tag is liked
     * @return returns the new rating for the new listing
     */
    public int firstLike(){
        rating = 5;
        LikeMultiplier = 0.6;
        DislikeMultiplier = 0.4;
        return 5;

    }

    /**
     * handles adding numbers to the listnumbers to keep track of what numbers a tag has
     * @param i list of numbers to add the listnumbers
     * @param alreadyIn whether the tag is already in the taglist
     */
    public void addNumbers(int[] i, boolean alreadyIn){
        for(Integer j: i){
            listNumbers.add(j);
        }
        if(alreadyIn) {
            rating = rating+ ((double)i.length / 100);
        }

    }

    /**
     * handles taking numbers from the listnumbers to make available for another tag
     * @param x the amount of numbers to take
     * @return the list of numbers that was taken
     */
    public int[] takeNumbers( int x) {
        int numberOfPlaces = (int)Math.floor(rating*100);//number of places the tag currently occupies
        int[] removed = new int[x];
        if(x<numberOfPlaces) {//checks if it has enough numbers to fulfill the request
            for (int i = x; i > 0; i--) {//remove the numbers from listnumber and adds them to removed
                removed[i - 1] = listNumbers.remove((numberOfPlaces - 1));
                numberOfPlaces--;
            }
            rating = (double) numberOfPlaces / 100;//reassigns rating to correspond to the number of places it occupies
            return removed;
        }
        else{//if not enough numbers just takes all the numbers in listnumbers
            for (int i = numberOfPlaces; i > 0; i--) {
                removed[i - 1] = listNumbers.remove((numberOfPlaces - 1));
                numberOfPlaces--;
            }
            rating = (double) numberOfPlaces / 100;
            return removed;
        }
    }

    public void addToList(){

    }

    /**
     * used for first dislike of a tag
     * @return the rating for the new tag added
     */
    public int firstDislike(){
        rating = -5;
        DislikeMultiplier = 0.6;
        LikeMultiplier = 0.4;
        return -5;
    }

    /**
     * handles liking of a tag, increases the rating and the like multiplier
     * @return how much the rating was raised by and how many numbers to add
     */
    public double like(){
        double raise = (1*LikeMultiplier);
        LikeMultiplier = LikeMultiplier+0.1;
        DislikeMultiplier = 1-LikeMultiplier;
        return raise;
    }

    /**
     * handles disliking of a tag, decreases the rating and increases the dislike multiplier
     * @return how much the rating was decreased by and how many numbers to take away
     */
    public double dislike(){
        double deficit = (1*DislikeMultiplier);
        DislikeMultiplier = DislikeMultiplier + 0.1;
        LikeMultiplier = 1-LikeMultiplier;
        return deficit;
    }

    /**
     * to string method, returns relavent information on the tag
     * @return all information on current tag
     */
    public String toString(){
        String r = "tag name: "+name+ " tag rating: "+ rating;
        return r;
    }
}
