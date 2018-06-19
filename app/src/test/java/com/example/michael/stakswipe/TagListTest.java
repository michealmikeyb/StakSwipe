package com.example.michael.stakswipe;

import android.app.Person;

import org.junit.Test;

import static org.junit.Assert.*;

public class TagListTest {
    TagList t = new TagList();

    @Test
    public void constructor(){
        double popPercent = t.getPercent("popular");
        assertEquals(100, popPercent, 0.1);
        assertEquals("tag name: popular tag rating: 0.0\n", t.toString());
    }

    @Test
    public void like() {
        PersonalTag p = new PersonalTag("test");
        t.like(p);
        assertEquals(5, p.rating, 0.01);
        assertEquals(t.getPercent("test"), (double)p.rating, 0.1);
        t.like(p);
        assertEquals(5.6, p.rating, 0.01);
        assertEquals(t.getPercent("test"), (double)p.rating, 0.1);
        t.dislike(p);
        t.like(p);
        assertEquals(5.6, p.rating, 0.01);
    }

    @Test
    public void dislike() {
        PersonalTag p = new PersonalTag("test");
        t.dislike(p);
        assertEquals(-5, p.rating, 0.01);
        assertEquals(t.getPercent("test"), 0, 0.1);
        t.dislike(p);
        assertEquals(-5.7, p.rating, 0.01);
        assertEquals(t.getPercent("test"), 0, 0.1);
        PersonalTag p2 = new PersonalTag("test2");
        t.like(p2);
        t.dislike(p2);
        assertEquals(4.6, p2.rating, 0.01);
    }

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

    @Test
    public void getList() {
        String s = "";
        for(int i = 0; i<10000; i++){
            s+="tag name: popular tag rating: 0.0\n";
        }
        assertEquals(s, t.getList());
    }

    @Test
    public void getArrayList() {
        assertArrayEquals(t.list, t.getArrayList());
    }

    @Test
    public void getPercent() {
        PersonalTag p1 = new PersonalTag("test1");
        PersonalTag p2 = new PersonalTag("test2");
        t.like(p1);
        t.like(p1);
        t.like(p2);
        assertEquals(5.6, t.getPercent("test1"), 0.1);
        assertEquals(5.0, t.getPercent("test2"), 0.1);
        double combined = t.getPercent("test1")+t.getPercent("test2")+t.getPercent("popular");
        assertEquals(100.0, combined, 0.1);
    }
}