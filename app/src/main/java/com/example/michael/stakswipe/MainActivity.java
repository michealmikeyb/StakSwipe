package com.example.michael.stakswipe;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.JsonReader;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.content.Intent;
import android.widget.VideoView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

public class MainActivity  extends AppCompatActivity implements com.example.michael.stakswipe.DownloadCallback, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, AddTagDialog.AddTagDialogListener{
    private TextView text;// text at the top that displays the title of the post
    private TextView botTxt;
    private GestureDetectorCompat gestureDetector;//gesture detector for detecting either a right or left swipe
    private ImageView iv; // image view for displaying the images of posts
    private ImageView botImg;//bottom image view
    private WebView topWeb;//displays webpages for the topcard in the case that the content is not an image
    private WebView botWeb;//displays webpages for the bottomcard
    private TextView topSub;//displays the subreddit and author of the post
    private TextView botSub;
    private CardView topCard;//the card layout containing the content that the user is seeing
    private CardView bottomCard;//the card ayout below the top card that is used to buffer

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    private boolean firstCheck = false;//boolean to see if it is the first time a json url has been tried to be downloaded
    private String jsonUrl;//holds the url of the json to be gotten from the server

    //settings for the tolerance of a left or right swipe
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private String botSubreddit;//the subreddit of the currently viewed posting
    public TagList list; // the taglist for storing the like and dislike information
    private String botAfter;//the id of the next post in the specific subreddit
    private String topSubreddit;//the sub of the content currently on the top card
    private String topAfter;//the after of the current listing on the top card
    private String botUrl;//the url of the bottom listing
    private String topUrl;
    private boolean imageLoading;//whether the image is loading or not
    private boolean isWebView;
    public SubList sublist;// stores the ids of the posts that will be pulled next for each subreddit
    private boolean isPopular = true;// shows whether the current post comes from the popular subreddit
    private String domain;
    private String source;//used for the addtag dialog
    private String topSource;
    private String botSource;

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
        topSub = (TextView) findViewById(R.id.subject1);
        botSub = (TextView) findViewById(R.id.subject2);
        topWeb = (WebView) findViewById(R.id.webView1);
        botWeb = (WebView) findViewById(R.id.webView2);
        botWeb.setWebViewClient(new WebViewClient());
        topWeb.setWebViewClient(new WebViewClient());
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        topWeb.getSettings().setJavaScriptEnabled(true);
        botWeb.getSettings().setJavaScriptEnabled(true);

        //initializing the list
        list = new TagList();
        sublist = new SubList();

        //pulling the last taglist and sublist from the previous session or creating a new place to store it
        tagSettings = getSharedPreferences("stakTagSaveList", Context.MODE_PRIVATE);
        placeSettings = getSharedPreferences("stakPlaceSaveList", Context.MODE_PRIVATE);
        tagPref = tagSettings.edit();
        placePref = placeSettings.edit();
        if(tagSettings.contains("stakTagSaveList"))
            restore();
        else
            save();




        text.setText("Loading...");
        newContent();




    }

    /**creates the options menu in the top right of the app
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /** handles the user clicking on one of the options in the options menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share: {//if the user selects share goes to the share method
                share();
                return true;
            }
            case R.id.add_tag://if the user selects add tag opens up a dialog where the user can enter the tag they want to add to their list
            {
                AddTagDialog dialog = new AddTagDialog();
                dialog.show(this.getSupportFragmentManager(), "Enter Tag");
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** handles the dialog for the user adding a tag to the list if the user cancels out
     * currently does nothing and just returns to the app
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
    /** handles when the user hits add tag within the dialog
     * adds the tag to the taglist
     * @param dialog
     * @param tag
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String tag, String source) {
        list.like(tag, source);
    }
    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_stak:
                if (checked) {
                    source = "stak";
                    System.out.println(source);
                    break;
                }
            case R.id.radio_reddit:
                if (checked)
                    source = "reddit";
                break;
        }
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
        if (json.contains("youtube.com")) {//video posts have different json formats so have to be handled differently
            int afterStart = json.lastIndexOf("after") + 9;//finds the start of the after pointer
            int afterEnd = json.lastIndexOf("before") - 4;

            int subStart = json.indexOf("\"subreddit\"") + 14;
            int subEnd = json.indexOf("\"selftext\"") - 3;
            System.out.println(json.substring(subStart, subEnd));
            botSubreddit = json.substring(subStart, subEnd);
            botAfter = json.substring(afterStart, afterEnd);
            newContent();
            return;
        } else {
            String title = "";
            try {//finds the third { of the json because thats where the listing data starts
                //int dataStart = json.indexOf('{', json.indexOf('{', json.indexOf('{', json.indexOf('{')+1)+1)+1);
                int dataStart = json.lastIndexOf("\"data\"") + 7;
                //finds the third } of the json because thats where the listing data ends
                int dataEnd = json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}', json.lastIndexOf('}') - 1) - 1) - 1) + 1;
                title = json.substring(dataStart, dataEnd); // creates a substring of the data section
            } catch (StringIndexOutOfBoundsException e) {
                if (!firstCheck) {//tries the same url again in case it was an error in downloading/ lost packets
                    firstCheck = true;
                    mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), jsonUrl);
                    mNetworkFragment.onCreate(null);
                    mNetworkFragment.setmCallback(this);
                    startDownload();//starts download for new listing
                } else {//if not changes firstcheck back to false and tries new content
                    firstCheck = false;
                    newContent();
                    return;
                }
            }

            int afterStart = json.lastIndexOf("after") + 9;//finds the start of the after pointer
            int afterEnd = json.lastIndexOf("before") - 4;


            Gson gson = new Gson();
            listing d;
            try {
                d = gson.fromJson(title, listing.class);//turns the data into a listing
            } catch (JsonSyntaxException e) {

                d = new listing(json.substring(afterStart, afterEnd));
                System.out.println(e.toString());
                return;
            }


            if (d != null) {
                System.out.println("updated");

                showBot(d);
            }


        }
    }
    public void showBot (listingInterface d)
        {
            botSubreddit = d.getTag();//sets the current subreddit
            botAfter = d.getAfter();
            botUrl = d.getUrl();
            domain = d.getDomain();
            System.out.println(d.getUrl());
            botTxt.setText(d.getTitle());//sets the text to the title
            botSub.setText("Posted on: " + d.getTag() + "\nBy: " + d.getAuthor());//gives context for the content


            //Picasso.with(this).load(d.getUrl()).into(botImg);//sets the image to the image from the url in the listing
            imageLoading = true;
            isWebView = false;//assume it is an image until it fails to load
            botWeb.loadUrl("about:blank");
            Glide.with(this).load(d.getUrl()).listener(new RequestListener<Drawable>() {
                @Override//if the content fails to load it is assumed that it is not an image and tries to load it in a webview
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    imageLoading = false;
                    isWebView = true;//needed for swapping the cards
                    handlePermissions(domain);
                    botWeb.loadUrl(botUrl);
                    botWeb.setVisibility(View.VISIBLE);
                    botImg.setVisibility(View.INVISIBLE);
                    return false;
                }

                @Override//if it can be loaded sets the webview to invisible
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    System.out.println("done loading");
                    botWeb.setVisibility(View.INVISIBLE);
                    botImg.setVisibility(View.VISIBLE);
                    isWebView = false;
                    imageLoading = false;
                    return false;
                }
            }).into(botImg);
        }


    public void handlePermissions(String domain){
        switch(domain){
            case "i.imgur.com":
                topWeb.getSettings().setJavaScriptEnabled(true);
                botWeb.getSettings().setJavaScriptEnabled(true);
                topWeb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                botWeb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                return;
            case "v.redd.it":
                topWeb.getSettings().setJavaScriptEnabled(true);
                botWeb.getSettings().setJavaScriptEnabled(true);
                return;
            case "i.redd.it":
                topWeb.getSettings().setJavaScriptEnabled(true);
                botWeb.getSettings().setJavaScriptEnabled(true);
                return;
            case "gfycat.com":
                topWeb.getSettings().setJavaScriptEnabled(false);
                botWeb.getSettings().setJavaScriptEnabled(false);
            default:
                return;
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
            tagPref.putString("stakTagSaveList", tagValue);
            tagPref.commit();

            placePref.putString("stakPlaceSaveList", placeValue);
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

        String tagLoad = tagSettings.getString("stakTagSaveList", "");
        list = gson.fromJson(tagLoad, TagList.class);

        String placeLoad = placeSettings.getString("stakPlaceSaveList", "");
        sublist = gson.fromJson(placeLoad, SubList.class);

        if(sublist.checkReset())//checks after if it is time for the sublist to reset
            sublist= new SubList();

        System.out.println("loaded from:"+tagLoad);


    }


    /**
     * handles left swipe action, dislikes the current subreddit, does a swipe left animation and
     * starts download of next listing
     */
    public void onLeftSwipe(){
        list.dislike(topSubreddit, topSource);//dislikes current subreddit
        //sets which subreddit it will set the after to
        sublist.setAfter(topSubreddit, topAfter);
        if(isPopular){
            sublist.setAfter("popular", topAfter);
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
                topCard.setVisibility(View.INVISIBLE);//set top card to invisible
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
        RelativeLayout layout = findViewById(R.id.mainLayout);//set the top views to a temp to swap them
        CardView tempCard = topCard;
        ImageView tempImg = iv;
        TextView tempText = text;
        TextView tempInfo = topSub;
        WebView tempWeb = topWeb;
        //swap them
        topCard = bottomCard;
        iv = botImg;
        text = botTxt;
        topSub = botSub;
        topWeb = botWeb;

        bottomCard = tempCard;
        botImg = tempImg;
        botTxt = tempText;
        botSub = tempInfo;
        botWeb = tempWeb;
        //change the subreddits and afters, the bottom doesn't need these to be swapped
        topSubreddit = botSubreddit;//bring the subreddits and afters up to date so the user is liking the one displayed at the top
        topAfter = botAfter;
        topUrl = botUrl;
        layout.bringChildToFront(topCard);
        /**topCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(topCard);
                ClipData.Item item = new ClipData.Item("left");
                ClipData data = ClipData.newPlainText("test", "left");
                topCard.startDrag(data, shadow, null, 0);
                topCard.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View view, DragEvent dragEvent) {
                        switch(dragEvent.getAction()){
                            case DragEvent.ACTION_DRAG_STARTED:
                                swapCards();
                        }
                        return false;
                    }
                });
                return false;
            }
        });**/
        bottomCard.setVisibility(View.VISIBLE);//make the card visible again


    }



    /**
     * handles the right swipe action, likes the current subreddit, does a right swiping animation
     * then starts a new listing on the bottom card
     */
    public void onRightSwipe(){
        list.like(topSubreddit, topSource);//likes current subreddit
        System.out.println("right");
        //checks if listing is from popular subreddit to assign after
        sublist.setAfter(topSubreddit, topAfter);
        if(isPopular){
            sublist.setAfter("popular", topAfter);
        }

        /**TranslateAnimation rightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        rightAnimation.setDuration(500);
        Animation.AnimationListener l = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                topCard.setVisibility(View.INVISIBLE);//set top card to invisible
                swapCards();
                newContent();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        rightAnimation .setAnimationListener(l);
        topCard.startAnimation(rightAnimation);**/

    }

    /**
     * chares the current url and subject of the current top card
     */
    public void share(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, text.getText());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, topUrl);
        startActivity(sharingIntent);
    }



    /**
     * pulls up a new tag from the tag list then checks if it has an after then makes a url to send to the
     * download manager to get the json to be parsed
     */
    public void newContent(){
        PersonalTag newSub = list.getTag();//gets the next sub and checks if it is popular
        isPopular = newSub.name.equals("popular");
        //checks if its already in sublist, if not just goes to first listing in subreddit, if it is then goes to the next listing
        if(newSub.source.equals("reddit")) {
            if(sublist.getAfter(newSub.name).equals("notIn")){
             mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/"+newSub+".json?limit=1");
             System.out.println("https://www.reddit.com/r/" + newSub.name + ".json?limit=1");
             jsonUrl = "https://www.reddit.com/r/" + newSub.name + ".json?limit=1";
             }
             else {
             mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub.name));
             System.out.println("https://www.reddit.com/r/" + newSub.name + ".json?limit=1;after=" + sublist.getAfter(newSub.name));
             jsonUrl = "https://www.reddit.com/r/" + newSub.name + ".json?limit=1;after=" + sublist.getAfter(newSub.name);
             }
             mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://127.0.0.1/stakSwipe/getListing.php?tag=picTag&place=0;");
             mNetworkFragment.onCreate(null);
             mNetworkFragment.setmCallback(this);
             startDownload();//starts download for new listing
        }
        else if(newSub.source.equals("stak")) {
            if(sublist.getAfter(newSub.name).equals("notIn")) {
                StakServerDownload s = new StakServerDownload(this);
                s.execute(newSub.name, "0");
            }
            else{
         StakServerDownload s = new StakServerDownload(this);
         s.execute(newSub.name, sublist.getAfter(newSub.name));
         try{
         Gson gson = new Gson();
         StakListing l = gson.fromJson(s.get(), StakListing.class);
         showBot(l);
         }
         catch(InterruptedException e){
         System.out.println(e);
         }
         catch(ExecutionException e){
         System.out.println(e);
         }
            }
        }

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
            if (e1.getY() - e2.getY() >  SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY
                    && Math.abs(e1.getX() - e2.getX())<SWIPE_MAX_OFF_PATH){
                share();
            }
            else if(Math.abs(e1.getY() - e2.getY())>SWIPE_MAX_OFF_PATH){
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

