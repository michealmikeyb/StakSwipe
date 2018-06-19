package com.example.michael.stakswipe;

import android.app.Person;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersonalTagTest {
    PersonalTag p = new PersonalTag("test");

    /**
     * tests isnegative by comparing it to a first dislike which should be negative
     * and making sure its less than 0
     */
    @Test
    public void isNegative() {
        p.firstDislike();//makes p's rating less than 0
        assert(p.isNegative());
        assert(p.rating<0);
        p.firstLike();
        assert(!p.isNegative());
    }

    /**
     * tests firstlike, should make the rating 5
     */
    @Test
    public void firstLike() {
        p.firstLike();
        assertEquals(5, p.rating, 0.1);

    }

    /**
     * tests addnumbers by giving it the 500 firstlike numbers than adding 3 more
     */
    @Test
    public void addNumbers() {
        int[] nums = new int[500];//the 500 first like numbers given after first like
        for(int i = 0; i<500; i++){
            nums[i] = i;
        }
        p.firstLike();
        p.addNumbers(nums, false);
        assertEquals(5, p.rating, 0.1);//tests to see if rating doesnt change if alreadyin set to false
        assertEquals(500,p.listNumbers.size());//tests size of list of numbers
        int[] nums2= {1,2,3};//adding 3 more numbers
        p.addNumbers(nums2, true);
        assertEquals(5.03, p.rating, 0.01);//test if rating changes if alreadyin set to true
        assertEquals(503, p.listNumbers.size());//tests size again

    }

    /**
     * tests takenumbers by first giving the tag its first like numbers than taking 5 of them
     */
    @Test
    public void takeNumbers() {
        int[] nums = new int[500];//give 500 first like numbers
        for(int i = 0; i<500; i++){
            nums[i] = i;
        }
        p.firstLike();
        p.addNumbers(nums, false);
        p.takeNumbers(5);//take 5 numbers
        assertEquals(4.95, p.rating, 0.01);//test ratings and size
        assertEquals(495, p.listNumbers.size());
    }


    /**
     * tests firstdislike method should give rating of -5
     */
    @Test
    public void firstDislike() {
        p.firstDislike();
        assertEquals(-5, p.rating, 0.01);
    }


}