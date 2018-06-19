package com.example.michael.stakswipe;

import android.app.Person;

import org.junit.Test;

import static org.junit.Assert.*;

public class TagListTest {
    TagList t = new TagList();

    /**
     * tests the constructor, should make a taglist full of popular tags
     */
    @Test
    public void constructor(){
        double popPercent = t.getPercent("popular");
        assertEquals(100, popPercent, 0.1);//test to see if it is full of popular tags
        assertEquals("tag name: popular tag rating: 0.0\n", t.toString());//tests to see if popular is the only tag in the list
    }

    /**
     * tests the like functionality of the taglist. likes an example tag with the name test and then sees if its
     * ratings change correspondingly
     */
    @Test
    public void like() {
        PersonalTag p = new PersonalTag("test");
        t.like(p);
        assertEquals(5, p.rating, 0.01);//first like should just bring it to 5
        assertEquals(t.getPercent("test"), (double)p.rating, 0.1);
        t.like(p);
        assertEquals(5.6, p.rating, 0.01);//second like tests the multiplier to see if its working
        assertEquals(t.getPercent("test"), (double)p.rating, 0.1);
        t.dislike(p);
        t.like(p);
        assertEquals(5.6, p.rating, 0.01);
    }

    /**
     * tests the dislike functionality of the taglist. dislikes an example tag with the name test and then sees if its
     * ratings change correspondingly
     */
    @Test
    public void dislike() {
        PersonalTag p = new PersonalTag("test");
        t.dislike(p);
        assertEquals(-5, p.rating, 0.01);//first dislike should bring it down to -5
        assertEquals(t.getPercent("test"), 0, 0.1);
        t.dislike(p);
        assertEquals(-5.7, p.rating, 0.01);//second one should bring it lower
        assertEquals(t.getPercent("test"), 0, 0.1);
        PersonalTag p2 = new PersonalTag("test2");
        t.like(p2);
        t.dislike(p2);
        assertEquals(4.6, p2.rating, 0.01);//test on a positive rating to see if it will go down
    }

    /**
     * tests gettag functionality by going through 10000 times to see if the ratings and percents
     * corespond roughly to how much they are being picked. since it is random there is a small
     * chance that this test will fail , but if run multiple times the majority should pass
     */
    @Test
    public void getTag() {
        PersonalTag p = new PersonalTag("test");
        t.like(p);
        int counter = 0;
        for( int i = 0; i<10000; i++){
            String x = t.getTag();
            if(x.equals("test")){
                counter++;
            }
        }
        assertEquals(500, counter, 100);
    }

    /**
     * tests the tostring method to see if it gives the right output
     */
    @Test
    public void testToString() {
        PersonalTag p = new PersonalTag("test");
        t.like(p);
        assertEquals("tag name: popular tag rating: 0.0\n" +
                "tag name: test tag rating: 5.0\n", t.toString());
        PersonalTag p2 = new PersonalTag("test2");
        t.dislike(p2);
        assertEquals("tag name: popular tag rating: 0.0\n" +
                "tag name: test tag rating: 5.0\n" +
                "tag name: test2 tag rating: -5.0\n", t.toString());
    }

    /**
     * tests the getlist function by creating a new string with 10000 popular tostrings
     */
    @Test
    public void getList() {
        String s = "";
        for(int i = 0; i<10000; i++){
            s+="tag name: popular tag rating: 0.0\n";
        }
        assertEquals(s, t.getList());
    }

    /**
     * basic getter method test
     */
    @Test
    public void getArrayList() {
        assertArrayEquals(t.list, t.getArrayList());
    }

    /**
     * tests the getpercent function, makes sure the percents are accurate and that they all add up to 100
     */
    @Test
    public void getPercent() {
        PersonalTag p1 = new PersonalTag("test1");
        PersonalTag p2 = new PersonalTag("test2");
        t.like(p1);
        t.like(p1);
        t.like(p2);
        assertEquals(5.6, t.getPercent("test1"), 0.1);//tests for accuracy of percents
        assertEquals(5.0, t.getPercent("test2"), 0.1);
        double combined = t.getPercent("test1")+t.getPercent("test2")+t.getPercent("popular");
        assertEquals(100.0, combined, 0.1);//test to see if all of them add up to 100
    }
}