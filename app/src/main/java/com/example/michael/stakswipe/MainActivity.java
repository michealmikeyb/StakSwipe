package com.example.michael.stakswipe;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.content.Intent;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity implements com.example.michael.stakswipe.DownloadCallback, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    private TextView text;// text at the top that displays the title of the post
    private TextView botTxt;
    private GestureDetectorCompat gestureDetector;//gesture detector for detecting either a right or left swipe
    private ImageView iv; // image view for displaying the images of posts
    private ImageView botImg;//bottom image view
    private CardView topCard;
    private CardView bottomCard;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    //settings for the tolerance of a left or right swipe
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private String currentSubreddit;//the subreddit of the currently viewed posting
    public TagList list; // the taglist for storing the like and dislike information
    private String currentAfter;//the id of the next post in the specific subreddit
    public SubList sublist;// stores the ids of the posts that will be pulled next for each subreddit
    private boolean isPopular = true;// shows whether the current post comes from the popular subreddit

    //places to long term store the taglist information and the subreddit list information
    private SharedPreferences.Editor tagPref;
    private SharedPreferences.Editor placePref;
    private SharedPreferences tagSettings;
    private SharedPreferences placeSettings;






    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing the views
        text = (TextView) findViewById(R.id.text1);
        iv = (ImageView) findViewById(R.id.imageView) ;
        botTxt = (TextView) findViewById(R.id.text2);
        botImg = (ImageView) findViewById(R.id.imageView2) ;
        topCard = (CardView) findViewById(R.id.topCard);
        bottomCard = (CardView) findViewById(R.id.bottomCard);
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);



        //initializing the list
        list = new TagList();
        sublist = new SubList();
        save();

        //pulling the last taglist and sublist from the previous session or creating a new place to store it
        tagSettings = getSharedPreferences("stakTagSave", Context.MODE_PRIVATE);
        placeSettings = getSharedPreferences("stakPlaceSave", Context.MODE_PRIVATE);
        tagPref = tagSettings.edit();
        placePref = placeSettings.edit();
        /**if(tagSettings.contains("stakTagSave"))
            restore();
        else
            save();**/




        text.setText("Start Swiping");
        newContent();




    }

    /**
     * starts the download of a link, goes to updateFromDownload method after done
     */
    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }


    public void updateFromDownload(String result) {
        text.setText(result);
    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     *
     * @param result
     */
    @Override
    public void updateFromDownload(Object result) {
        String json = (String) result;
        save();//saves the current taglist and sublist
        if(json.contains("youtube.com")){//video posts have different json formats so have to be handled differently
            newContent();
        }
        else {
            String title = "";
            try {//finds the third { of the json because thats where the listing data starts
                int dataStart = json.indexOf('{', json.indexOf('{', json.indexOf('{', json.indexOf('{')+1)+1)+1);
                //finds the third } of the json because thats where the listing data ends
                int dataEnd = json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}')-1)-1)-1)+1;
                title = json.substring(dataStart, dataEnd); // creates a substring of the data section
            }
            catch(StringIndexOutOfBoundsException e){
                int start =  json.indexOf('{', json.indexOf('{', json.indexOf('{', json.indexOf('{')+1)+1)+1);
                int end = json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}')-1)-1)-1)+1;
                System.out.println("out of bounds, start: "+start+" end: "+end);
            }

            int afterStart = json.lastIndexOf("after")+9;//finds the start of the after pointer
            int afterEnd = json.lastIndexOf("before")-4;



            Gson gson = new Gson();
            listing d;
            try {
                d = gson.fromJson(title, listing.class);//turns the data into a listing
            } catch (JsonSyntaxException e) {

                d = new listing();
                System.out.println(e.toString());
            }




                currentSubreddit = d.getSubreddit();//sets the current subreddit
                currentAfter = json.substring(afterStart, afterEnd);


                botTxt.setText(d.getTitle());//sets the text to the title
                //Picasso.with(this).load(d.getUrl()).into(botImg);//sets the image to the image from the url in the listing
                Glide.with(this).load(d.getUrl()).into(botImg);

        }

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                text.setText("error");
                break;
            case Progress.CONNECT_SUCCESS:
                text.setText("connected");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
        System.out.println("finished");
    }

    /**
     * gets the domain of a full json of a listing
     * @param j
     * @return
     */
    public String getDomain(String j){
        int start = j.lastIndexOf("\"domain\":")+11;
        int end = j.lastIndexOf("\"media_embed\":") -3;
        return j.substring(start, end);
    }

    /**
     * saves the current taglist and sublist to saved preerences
     * @return true if the save succeeded
     */
    public boolean save(){
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        try{
            // converts the lists into jsons
            String tagValue = gson.toJson(list);
            String placeValue = gson.toJson(sublist);

            //saves the json that was made
            tagPref.putString("stakTagSave", tagValue);
            tagPref.commit();

            placePref.putString("stakPlaceSave", placeValue);
            placePref.commit();
            return true;

        }
        catch (Exception e) {
            System.out.println("save error "+ e.toString());
            return false;
        }
    }

    /**
     * restores the previous sublist and taglist from saved preferences
     */
    public void restore(){
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        String tagLoad = tagSettings.getString("stakTagSave", "");
        list = gson.fromJson(tagLoad, TagList.class);

        String placeLoad = placeSettings.getString("stakPlaceSave", "");
        sublist = gson.fromJson(placeLoad, SubList.class);

        if(sublist.checkReset())//checks after if it is time for the sublist to reset
            sublist= new SubList();

        System.out.println("loaded from:"+placeLoad);


    }


    /**
     * handles left swipe action, dislikes the current subreddit, does a swipe left animation and
     * starts download of next listing
     */
    public void onLeftSwipe(){
        list.dislike(new PersonalTag(currentSubreddit));//dislikes current subreddit
        //sets which subreddit it will set the after to
        sublist.setAfter(currentSubreddit, currentAfter);
        if(isPopular){
            sublist.setAfter("popular", currentAfter);
        }
        //left swipe animation
        TranslateAnimation leftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        leftAnimation.setDuration(500);//duration of the animation in milliseconds
        Animation.AnimationListener l = new Animation.AnimationListener() {//listener to tell when animation has ended
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {//once animation has ended swap the cards then start to load new content on the bottom card
                swapCards();
                newContent();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        leftAnimation.setAnimationListener(l);
        topCard.startAnimation(leftAnimation);
    }

    /**
     * puts the text and picture from the bottom card on to the top card and keeps the top card invisible
     * while the process is happening
     */
    public void swapCards(){

        topCard.setVisibility(View.INVISIBLE);//set top card to invisible
       iv.setImageDrawable(botImg.getDrawable());//put the picture of the bottom card on the top card
       text.setText(botTxt.getText());//do the same with the text

        topCard.setVisibility(View.VISIBLE);//make it visible again
    }

    /**
     * handles the right swipe action, likes the current subreddit, does a right swiping animation
     * then starts a new listing on the bottom card
     */
    public void onRightSwipe(){
        System.out.println("right");
        list.like(new PersonalTag(currentSubreddit));//likes current subreddit
        //checks if listing is from popular subreddit to assign after
        sublist.setAfter(currentSubreddit, currentAfter);
        if(isPopular){
            sublist.setAfter("popular", currentAfter);
        }

        TranslateAnimation rightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        rightAnimation.setDuration(500);
        Animation.AnimationListener l = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                swapCards();
                newContent();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        rightAnimation .setAnimationListener(l);
        topCard.startAnimation(rightAnimation);

    }



    /**
     * pulls up a new tag from the tag list then checks if it has an after then makes a url to send to the
     * download manager to get the json to be parsed
     */
    public void newContent(){
        String newSub = list.getTag();//gets the next sub and checks if it is popular
        isPopular = newSub.equals("popular");
        //checks if its already in sublist, if not just goes to first listing in subreddit, if it is then goes to the next listing
        if(sublist.getAfter(newSub).equals("notIn")){
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/"+newSub+".json?limit=1");
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1");
        }
        else {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
        }
        mNetworkFragment.onCreate(null);
        mNetworkFragment.setmCallback(this);
        startDownload();//starts download for new listing
    }


    /**
     * Notified when a single-tap occurs.
     * <p>
     * Unlike ] this
     * will only be called after the detector is confident that the user's
     * first tap is not followed by a second tap leading to a double-tap
     * gesture.
     *
     * @param e The down motion event of the single-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    /**
     * Notified when a double-tap occurs.
     *
     * @param e The down motion event of the first tap of the double-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    /**
     * Notified when an event within a double-tap gesture occurs, including
     * the down, move, and up events.
     *
     * @param e The motion event that occurred during the double-tap gesture.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        sublist = new SubList();
        return true;
    }

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    /**
     * The user has performed a down {@link MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link MotionEvent}
     * that trigged it.
     *
     * @param e The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
     * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
     * the x and y axis in pixels per second. calls onLeftSwipe when swiping lef, calls
     * onRightSwipe when rightswiping
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onLeftSwipe();
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                list.like(new PersonalTag(currentSubreddit));
                onRightSwipe();
            }
        } catch (Exception e) {

        }
        return false;

    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
