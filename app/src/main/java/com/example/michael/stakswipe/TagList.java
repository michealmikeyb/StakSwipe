package com.example.michael.stakswipe;

import java.util.ArrayList;
import java.util.Random;
import android.content.SharedPreferences;

/**
 * handles a list of tags that is randomly picked from to determine the tag/subreddit
 * to be pulled up
 * Created by miche_000 on 7/24/2017.
 */

public class TagList {
    PersonalTag[] list;//a 10,000 large list filled with tags to randomly picked from
    ArrayList<PersonalTag> allTags;// a list of all the tags currently in the list
    TagDeficit def;// the deficit which handles the available numbers that can be reassigned

    /**
     * initializes a new taglist filled with the popular tag by default
     */
    public TagList(){
        allTags = new ArrayList<PersonalTag>();//initialize the list
        list = new PersonalTag[10000];
        PersonalTag popular = new PersonalTag("popular");
        //fill it with the popular tag
        for(int i = 0; i<10000; i++){
            list[i] = popular;
        }

        allTags.add(popular);
        //initialize the deficit , making 80% of the spots open, 20% will always be occupied by the popular tag
        int[] deflist = new int[8000];

        int j = 0;
        for(int i : deflist){
            deflist[j] = j;
            j++;
        }
        def = new TagDeficit(80, deflist);



    }

    /**
     * handles liking of a certain tag, increases its rating and
     * assigning it more numbers within the list
     *
     * @param tag the tag that is being liked
     */
    public void like(String tag){
        if(tag == null)
            return;
        boolean alreadyIn = false;//whether the tag is already in
        int allTagsPlace = -1;
        for(int i = 0; i<allTags.size();i++){//checks to see if the tag is already in the alltags list
            if(allTags.get(i).name!=null && tag!=null&& allTags.get(i).name.equals(tag)){
                allTagsPlace = i;
                alreadyIn = true;
                break;
            }
        }
        double raise = 0;//the amount to raise the rating by
        if(alreadyIn){//if its already in then just likes it , if not gives first like and adds it to alltags
            raise = allTags.get(allTagsPlace).like();
        }
        else if(tag!=null) {
            PersonalTag p = new PersonalTag(tag);
            raise = p.firstLike();
            allTags.add(p);
            allTagsPlace = allTags.size()-1;

        }
        if(def.deficit>0) {//checks if there are any numbers available
            if (raise < def.deficit) {//checks if the enough numbers are available to give full raise
                int[] numbers = def.give((int) (100 * raise));//multiply by 100 because it is given in rating not number of indexes
                allTags.get(allTagsPlace).addNumbers(numbers, alreadyIn);//add the numbers to the tags list of numbers
                for (int i : numbers) {//adds the tag to the indexes given by the deficit
                    list[i] = allTags.get(allTagsPlace);
                }
            } else {//if not enough deficit just gives all available deficit
                int newRaise = (int) (def.deficit) * 100;//takes all the deficit
                int[] numbers = def.give(newRaise);
                allTags.get(allTagsPlace).addNumbers(numbers, alreadyIn);//adds the numbers to the tags list of  numbers
                for (int i : numbers) {//assigns the given indexes in the list to the tag
                    list[i] = allTags.get(allTagsPlace);
                }
            }
        }
    }

    /**
     * handles disliking of a tag, reducing its rating and
     * taking away the amount of indexes it holds in the list
     * and making them available for other tags
     * @param tag the tag to dislike
     */
    public void dislike(String tag){
        if(tag==null)
            return;
        boolean alreadyIn = false;//whether the tag is already in
        int allTagsPlace = -1;
        for(int i = 0; i<allTags.size();i++){//checks to see if the tag is already in the alltags list
            if(allTags.get(i).name!=null && tag!=null&& allTags.get(i).name.equals(tag)){
                allTagsPlace = i;
                alreadyIn = true;
                break;
            }
        }
        double deficit;//the amount the rating will be decreased by and the number of indexes to take away/100
        if(alreadyIn) {
            System.out.println("dislike");
            deficit = allTags.get(allTagsPlace).dislike();


            if (allTags.get(allTagsPlace).isNegative()) {//if negative has no numbers in the list and the rating can just be decreased.
                allTags.get(allTagsPlace).rating -= allTags.get(allTagsPlace).dislike();

            }

            else {

                if (def.deficit - deficit < 10000) {//check to see if adding that many numbers to the deficit will go over 10,000
                    int[] nums = allTags.get(allTagsPlace).takeNumbers((int) (deficit * 100));
                    def.take(nums);

                }
                else {
                    int newDeficit = (int) (10000 - def.deficit);
                    def.take(allTags.get(allTagsPlace).takeNumbers(newDeficit));
                }
            }
        }

        else if(tag!=null){//gives first dislike making rating -5 and adding it to the alltags list
            PersonalTag p = new PersonalTag(tag);
            p.firstDislike();
            allTags.add(p);
        }

    }

    /**
     * gives a random tag on the list
     * @return a random tag from the list
     */
    public String getTag(){
        Random generator = new Random();
        int number = generator.nextInt(10000);

        while(list[number].name==null) {
            list[number] = new PersonalTag("popular");
            number = generator.nextInt(10000);
            int[] nullNumber = {number};
            def.take(nullNumber);
        }


        return list[number].name;
    }

    /**
     * gives information on all the tags currently in the list
     * @return
     */
    public String toString(){
        String r = "";
        for(PersonalTag t : allTags){
            r+=(t.toString()+"\n");
        }
        return r;
    }

    /**
     * gives a string representing the list of personal tags that the
     * gettag function is selecting from
     * @return a string containing all the tags in the list in the order there in the list
     */
    public String getList(){
        String r = "";
        for(PersonalTag t : list){
            r+=(t.toString()+"\n");
        }
        return r;
    }

    /**
     * gives the list
     * @return the list containing all the information on the tags
     */
    public PersonalTag[] getArrayList(){
        return list;
    }

    public double getPercent(String n){

        double counter = 0;
        for(PersonalTag p: list){
            if(p.name.equals(n))
                counter+=0.01;
        }
        return counter;
    }

    /**removes all instances of a certain tag from the tag list
     * @param tag the name of the tag to be removed
     */
    public void removeTag(String tag){
        for(PersonalTag p: allTags)//remove it from alltags
            if(p.name.equals(tag))
                allTags.remove(p);
        for(int i = 0;i<list.length;i++){//go through the list
            if(list[i].name!=null&&list[i].name.equals(tag)){
                list[i] = new PersonalTag("popular");//replace the tags place in the list with popular
                int[] removeNums = {i};//add that to the deficit to free up the numbers
                def.take(removeNums);
            }
        }
    }
}
