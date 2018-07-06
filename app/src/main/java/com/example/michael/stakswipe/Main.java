package com.example.michael.stakswipe;

public class Main {
    public static void main(String[] args){


        TagList list = new TagList();

        list.like("pics");
        list.like("pics");
        list.like("movies");
        list.dislike("music");
        list.dislike("movies");

        for(int i = 0; i<100; i++)
            list.like("pics");

        for(int i = 0; i<10;i++)
            list.dislike("movies");
        list.like("pics");
        list.like("pics");
        list.like("pics");
        list.like("pics");


        int movieCount = 0;
        int picCount = 0;



    }
}
