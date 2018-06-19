package com.example.michael.stakswipe;

import org.junit.Test;

import static org.junit.Assert.*;

public class TagDeficitTest {

    /**
     * tests the construtor by making a deficit of 100
     */
    public void constructor(){
        int[] nums =new int[100];
        for (int i = 0; i<100; i++){//fill an array with numbers for the deficit
            nums[i] = i;
        }
        TagDeficit d = new TagDeficit(1,nums );
        assertEquals(1, d.deficit, 0.1);
    }

    /**
     * tests the give method by giving 10 numbers then tests giving over amount in deficit
     */
    @Test
    public void give() {
        int[] nums =new int[100];//construct a new deficit
        for (int i = 0; i<100; i++){
            nums[i] = i;
        }
        TagDeficit d = new TagDeficit(1,nums );

        d.give(10);
        assertEquals(0.9, d.deficit, 0.01);//give 10 numbers and see if deficit changes
        d.give(100);
        assertEquals(0, d.deficit, 0.02);//gives 100 numbers and sees if deficit goes to 0

    }

    /**
     * tests the take method by giving the deficit 3 numbers
     */
    @Test
    public void take() {
        int[] nums =new int[100];//construct a new deficit
        for (int i = 0; i<100; i++){
            nums[i] = i;
        }
        TagDeficit d = new TagDeficit(1,nums );

        d.give(10);
        assertEquals(0.9, d.deficit, 0.01);//free up space and check deficit
        int[] i = {91, 92, 93};
        d.take(i);
        assertEquals(0.93, d.deficit, 0.01);//give numbers 91, 92 and 93
    }
}