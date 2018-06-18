package com.example.michael.stakswipe;

/**
 * handles the numbers that are available in the taglist
 * Created by miche_000 on 7/24/2017.
 */

public class TagDeficit {
    public double deficit;//the amount of numbers available to take from if a tag needs numbers
    public int[] places;//the numbers that are currently available

    public TagDeficit(double d, int[] i){
        deficit = d;
        places = i;
    }

    /**
     * gives a certain amount of numbers, taking them away from the deficit to be assigned to a new tag
     * @param j the amount of numbers to take from the deficit
     * @return the numbers that were taken and are to be reassigned
     */
    public int[] give(int j){
        int numbers = (int)(Math.floor(deficit*100));
        int[] numToGive = new int[j];
        int x = 0;
        for(int i = numbers; i>numbers-j; i--){
            numToGive[x] = places[i-1];
            places[i-1] = 0;
            deficit-=0.01;
            x++;
        }
        return numToGive;
    }

    /**
     * takes a certain amount of numbers from a certain tag and makes them available to other tags
     * @param j the numbers to make available for other tags
     */
    public void take(int[] j){
        int numbers = (int)Math.floor(deficit*100);
        for(int i: j){
            places[numbers] = i;
            numbers++;
        }
        deficit= ((double)numbers)/100;

    }
}
