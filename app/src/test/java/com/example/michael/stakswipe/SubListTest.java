package com.example.michael.stakswipe;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubListTest {
    SubList l = new SubList();
    @Test
    public void getAfter() {
        l.setAfter("popular", "popafter1");
        assertEquals("popafter1", l.getAfter("popular"));
        l.setAfter("test", "testafter1");
        assertEquals("testafter1", l.getAfter("test"));
        l.setAfter("popular", "popafter2");
        assertEquals("popafter2", l.getAfter("popular"));
    }

    @Test
    public void setAfter() {
    }

    @Test
    public void add() {
    }
}