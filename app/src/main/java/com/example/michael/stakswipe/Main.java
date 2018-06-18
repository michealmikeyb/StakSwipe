package com.example.michael.stakswipe;

public class Main {
    public static void main(String[] args){
        PersonalTag pictures = new PersonalTag("pics");
        PersonalTag movies = new PersonalTag("movies");
        PersonalTag music = new PersonalTag("music");

        TagList list = new TagList();

        list.like(pictures);
        list.like(pictures);
        list.like(movies);
        list.dislike(music);
        list.dislike(movies);

        for(int i = 0; i<100; i++)
            list.like(pictures);

        for(int i = 0; i<10;i++)
            list.dislike(movies);
        list.like(pictures);
        list.like(pictures);
        list.like(pictures);
        list.like(pictures);


        int movieCount = 0;
        int picCount = 0;

        for(PersonalTag p: list.getArrayList()){
            if(p.name.equals("pics"))
                picCount++;
            else if(p.name.equals("movies"))
                movieCount++;
        }

        System.out.print("movies: "+movieCount+"pics: "+ picCount);


    }
}
