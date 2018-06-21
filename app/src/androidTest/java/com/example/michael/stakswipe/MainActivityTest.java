package com.example.michael.stakswipe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import android.support.test.rule.ActivityTestRule;
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity m = null;
    @Before
    public void setUp() throws Exception {
        m= testRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        m = null;
    }

    /**
     * tests the save and restore functionality
     */
    @Test
    public void save() {
        assertEquals("tag name: popular tag rating: 0.0\n", m.list.toString());
        m.list.like(new PersonalTag("test"));
        assertEquals("tag name: popular tag rating: 0.0\n" +
                "tag name: test tag rating: 5.0\n", m.list.toString());

        m.save();
        m.list = new TagList();
        m.restore();
        assertEquals("tag name: popular tag rating: 0.0\n" +
                "tag name: test tag rating: 5.0\n", m.list.toString());
    }


}