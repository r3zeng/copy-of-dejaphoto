package com.example.mingchengzhu.dejaphoto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.io.File;
import android.content.res.Resources;
import android.net.Uri;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    //swipes
    public enum SwipeDirection{
        right, left, neither
    }
    SwipeDirection lastSwipe = SwipeDirection.neither;         

    // Used for logging
    private static final String TAG = "MainActivity";

    // Used with the photo chooser intent
    private static final int RESULT_LOAD_IMAGE = 1;

    // Used to receive an address result from FetchAddressIntentService
    private AddressResultReceiver mResultReceiver;

    // Used for tracking system time and location
    private Tracker tracker = new Tracker();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private AutoSwitch auto_switch;
    TextView textView;
    TextView textView2;

    // true if we are currently showing the user a message about having no photos
    boolean noPhotosModeEnabled = false;

    private PopupWindow popup;
    // Field for setting panel
    private boolean Deja_Time = true;
    private boolean Deja_Date = true;
    private boolean Deja_Location = true;
    private boolean Deja_Karma = true;
    private int Deja_refresh_time = 10000; //10 seconds
    private final Handler auto_switch_handler = new Handler();
    PreviousImage previousImage;
    DejaPhoto CurrentPhoto;

    /**
     * turns on/off a message about having no photos
     *
     * @param enabled true if we should be showing a message about having no photos
     */
    void setNoPhotosModeEnabled(boolean enabled) {
        noPhotosModeEnabled = enabled;

        ImageView background = (ImageView) findViewById(R.id.backgroundImage);

        if (enabled) {
            Log.d(TAG, "enabling no photos mode");

            int resID = getResources().getIdentifier("default_background", "drawable",  getPackageName());
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(resID) + '/' +
                    getResources().getResourceTypeName(resID) + '/' +
                    getResources().getResourceEntryName(resID) );
            background.setImageURI(uri);

        } else {
            Log.d(TAG, "disabling no photos mode");

        }

        background.invalidate();
    }

    /**
     * Used to receive address results from FetchAddressIntentService
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            /*
            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }
            */

            if (resultCode == Constants.FETCH_ADDRESS_SUCCESS) {
                TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
                locationTextView.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            } else {
                Log.e(TAG, "location reverse geocoding failed");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        previousImage = new PreviousImage();
        CurrentPhoto = null;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /* The following is used to implement swiping functionality */
        ImageView backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

        backgroundImage.setOnTouchListener(new OnSwipeListener(MainActivity.this){

            @Override
            public void onSwipeRight(){
                //put switch wallpaper method here
                SwipeRight();
                if(auto_switch != null){
                    auto_switch_handler.removeCallbacks(auto_switch);
                    auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
                }
            }
            @Override
            public void onSwipeTop() {
                //put addKarma method here
                if(CurrentPhoto != null){
                    CurrentPhoto.setKarma(true);
                }
                if(auto_switch != null){
                    auto_switch_handler.removeCallbacks(auto_switch);
                    auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
                }
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSwipeLeft() {
                SwipeLeft();
                if(auto_switch != null){
                    auto_switch_handler.removeCallbacks(auto_switch);
                    auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
                }
            }
            @Override
            public void onSwipeDown() {
                //put release method here
                if(CurrentPhoto != null){
                    CurrentPhoto.setReleased(true);
                }
                if(auto_switch != null){
                    auto_switch_handler.removeCallbacks(auto_switch);
                    auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
                }
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });


        /* The following is used to update screen based on location */
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // call switch screen
                tracker.updateLocation(location);
                tracker.updateTime();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        // specified refresh time, 500 meters
        locationManager.requestLocationUpdates(locationProvider, Deja_refresh_time, 500, locationListener);


        /* The following is used to implement auto-switch background */
        auto_switch = new AutoSwitch(this, auto_switch_handler, Deja_refresh_time);

        /* Start the runnable task*/
        auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);

        CurrentPhoto = getNextRandomImage();
        // if CurrentPhoto is null, it will display a message telling the user there are no photos
        setBackgroundImage(CurrentPhoto);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_time) {
            if(Deja_Time){
                item.setTitle("Time Off");
                Deja_Time = false;
            }else{
                item.setTitle("Time On");
                Deja_Time = true;
            }
        } else if (id == R.id.nav_date) {
            if(Deja_Date){
                item.setTitle("Date Off");
                Deja_Date = false;
            }else{
                item.setTitle("Date On");
                Deja_Date = true;
            }
        } else if (id == R.id.nav_location) {
            if(Deja_Location){
                item.setTitle("Location Off");
                Deja_Location = false;
            }else{
                item.setTitle("Location On");
                Deja_Location = true;
            }
        }else if (id == R.id.nav_karma){
            if(Deja_Karma){
                item.setTitle("Karma Off");
                Deja_Karma = false;
            }else{
                item.setTitle("Karma On");
                Deja_Karma = true;
            }
        }else if(id == R.id.add_photo){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            AddPhoto();
        }else if(id == R.id.change_frequency){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            changeFrenquencyPopUp();
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFrenquencyPopUp(){
        LayoutInflater inflator2 = (LayoutInflater) getApplication().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)  inflator2.inflate(R.layout.change_freqency_pop_up, null);

        popup = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relative_layout);
        popup.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);
        TextView instruction = (TextView) popup.getContentView().findViewById(R.id.textView4);
        EditText field = (EditText) popup.getContentView().findViewById(R.id.change_frequency_edittext);
        instruction.setTextColor(Color.BLACK);
        field.setTextColor(Color.BLACK);
        Button confirm = (Button) popup.getContentView().findViewById(R.id.frequency_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText mEdit = (EditText) popup.getContentView().findViewById(R.id.change_frequency_edittext);
                try {//catch overflow
                    if (!mEdit.getText().toString().equals("")) {//no null strings
                        Deja_refresh_time = (Integer.valueOf(mEdit.getText().toString()))*1000;
                        if(auto_switch != null){
                            auto_switch.setTime(Deja_refresh_time);
                            auto_switch_handler.removeCallbacks(auto_switch);
                            auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
                        }
                        popup.dismiss();
                    }
                }catch(Exception e){
                    popup.dismiss();
                }
            }
        });

        Button cancel = (Button) popup.getContentView().findViewById(R.id.frequency_cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                popup.dismiss();
            }
        });
    }

    public void AddPhoto(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            CurrentPhoto = DejaPhoto.addPhotoWithUri(selectedImage, this);
            previousImage.swipeRight(CurrentPhoto);

            //Andy is Testing Writing to File
            StateCodec.addDejaPhotoToSC(this, "stateCodec.txt", CurrentPhoto);
            setBackgroundImage(CurrentPhoto);

            /* Setting wallpaper */
            // converting uri to bitmap
            SetWallpaper(CurrentPhoto);

            // reset timer
            if(auto_switch != null){
                auto_switch_handler.removeCallbacks(auto_switch);
                auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);
            }

        }
    }

    /* Setting wallpaper method*/
    private void SetWallpaper(final DejaPhoto photo) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Uri uri = photo.getUri();
                InputStream image_stream = null;
                Bitmap bitmap = null;
                try {
                    image_stream = getContentResolver().openInputStream(uri);
                }
                catch (FileNotFoundException e){
                    // logging message
                }
                if(image_stream != null){
                    bitmap= BitmapFactory.decodeStream(image_stream);
                }
                // setting wallpaper with the converted bitmap
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplicationContext());
                try {
                    if(bitmap != null) {
                        myWallpaperManager.setBitmap(bitmap);
                    }
                }
                catch (IOException e) {
                    // logging message
                }
            }
        });
    }

    /**
     * Displays a photo in the background, along with its location
     *
     * @param photo the photo to display in the background
     */
    public void setBackgroundImage(DejaPhoto photo) {
        if (photo == null) {
            setNoPhotosModeEnabled(true);
            return;
        }

        setNoPhotosModeEnabled(false);

        ImageView background = (ImageView) findViewById(R.id.backgroundImage);
        background.setImageURI(photo.getUri());
        background.invalidate();

        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationTextView.setText("");

        Location location = photo.getLocation();
        if (location != null) {
            if (mResultReceiver == null) {
                mResultReceiver = new AddressResultReceiver(new Handler());
            }

            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
            startService(intent);
        }
    }

    /**
     * sets an image as the background
     *
     * @param file java.io.File that contains path to image
     */
    public void setBackgroundImage(File file){
        Uri uri = Uri.fromFile(file);
        ImageView background = (ImageView) findViewById(R.id.backgroundImage);
        background.setImageURI(uri);
    }

    /**
     * Overloaded function to set backgound image
     *
     * @param uri path to image
     */
    public void setBackgroundImage(Uri uri){
        ImageView background = (ImageView) findViewById(R.id.backgroundImage);
        background.setImageURI(uri);
    }

    /**
     * gets the next Image to display
     * should be called on right swipe and by the auto-switcher
     */
    public DejaPhoto getNextRandomImage(){

        DejaPhoto[] list = DejaPhoto.getCurrentSearchResults();
        if(list == null || list.length == 0){
            System.err.println("Error: getting next image from empty album");
            return null;
        }

        double largestWeight = -1;
        DejaPhoto selectedPhoto = null;

        for(int i = 0; i < list.length; i++){
            DejaPhoto currentPhoto = list[i];
            double photoWeight = getTotalPhotoWeight(currentPhoto);
            if(photoWeight > largestWeight ){
                selectedPhoto = currentPhoto;
                largestWeight = photoWeight;
            }
        }

        return  selectedPhoto;
    }

    /**
     * helper function for getNextRandomImage
     * gets the weight for the probality a photo is displayed
     *
     * @param photo deja photo object
     * @return a value repersenting the likelyness this photo is to be displayed as the background.
     *  note: This value is not a percentage and should be compared relative to other photo weights
     */
    private double getTotalPhotoWeight(DejaPhoto photo){
        Random rand = new Random();
        double rand_value = rand.nextDouble();
        return rand_value * getTimeWeight(photo) * getKarmaWeight(photo) * getRelasedWeight(photo)
                * getDateWeight(photo) * getLocationWeight(photo) * getRecentWeight(photo)
                * getSameDayWeight(photo) * getLastPhotoWeight(photo);
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return time weight
     */
    private double getTimeWeight(DejaPhoto photo){
        if(!Deja_Time){ /*time from deja mode disabled*/
            return 1; //base weight
        }else if(tracker == null || tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        }else{
            long SystemTime = tracker.getTime();
            long PhotoTime = photo.getTime();

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_2_HOURS = 7200000;

            long difference = Math.abs(SystemTime - PhotoTime) % MILLISECONDS_IN_DAY;

            if(difference < MILLISECONDS_IN_2_HOURS){
                return 2;
            }else{
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return date weight
     */
    private double getDateWeight(DejaPhoto photo){
        if(!Deja_Date){
            return 1;
        }else if(tracker == null || tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid datat
        }else{
            long SystemTime = tracker.getTime();
            long PhotoTime= photo.getTime();

            long difference = Math.abs(SystemTime - PhotoTime);

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_MONTH = 30 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_3_MONTH = 3 * MILLISECONDS_IN_MONTH;
            final long MILLISECONFS_IN_6_MONTH = 6 * MILLISECONDS_IN_MONTH;

            if(difference < MILLISECONDS_IN_DAY){
                return 2;
            }else if(difference < MILLISECONDS_IN_WEEK){
                return 1.7;
            }else if(difference < MILLISECONDS_IN_MONTH){
                return 1.4;
            }else if(difference < MILLISECONDS_IN_3_MONTH){
                return  1;
            }else if(difference < MILLISECONFS_IN_6_MONTH){
                return 0.7;
            }else{
                return 0.5;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return location weight
     */
    private double getLocationWeight(DejaPhoto photo){
        if(!Deja_Location){
            return 1; //base weight
        }else if(tracker == null || tracker.getLocation() == null || photo.getLocation() == null) {
            return 1;//invalid data
        }else{
            Location SystemLocation = tracker.getLocation();
            Location PhotoLocation = photo.getLocation();

            double DistanceInMeters = SystemLocation.distanceTo(PhotoLocation);

            if(DistanceInMeters < 200){
                return 2;
            }else{
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return Karma weight
     */
    private double getKarmaWeight(DejaPhoto photo){
        if(!Deja_Karma){
            return 1;
        }else{
            if(photo.getKarma()){
                return 2;
            }else{
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return release weight
     */
    private double getRelasedWeight(DejaPhoto photo){
        if(photo.getReleased()){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * shoulf not be called elsewhere
     *
     * @param photo
     * @return recent weight
     */
    private double getRecentWeight(DejaPhoto photo){
        if(previousImage != null && previousImage.PhotoPreviouslySeen(photo)){
            return 0.1;
        }else {
            return 1;
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @param photo
     * @return same day weight 
     */
    private double getSameDayWeight(DejaPhoto photo){
        if(!Deja_Date) {
            return 1;
        }else if(tracker == null || tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        }else {
            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;

            long CurrentTime = tracker.getTime();
            long PhotoTime = photo.getTime();

            CurrentTime = CurrentTime % MILLISECONDS_IN_WEEK;
            PhotoTime = PhotoTime % MILLISECONDS_IN_WEEK;

            long CurrentDay = CurrentTime / MILLISECONDS_IN_DAY;
            long PhotoDay = PhotoTime / MILLISECONDS_IN_DAY;

            if (CurrentDay == PhotoDay) {
                return 2;
            } else {
                return 1;
            }
        }
    }
    
    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     * 
     * @param photo
     * @return 0% chance of getting same photo twice unless only 1 photo
     */
    private double getLastPhotoWeight(DejaPhoto photo){
        if(previousImage == null || previousImage.getNumberofPhoto() == 1){
            return 1;
        }else if(previousImage.getLastPhoto().equals(photo)){
            return 0;
        }else{
            return 1;
        }
    }
    public void SwipeRight(){
        //put switch wallpaper method here
        CurrentPhoto = getNextRandomImage();
        if(CurrentPhoto != null){
            if(lastSwipe ==  SwipeDirection.left){
                CurrentPhoto = getNextRandomImage();
            }
            setBackgroundImage(CurrentPhoto);
            SetWallpaper(CurrentPhoto);

            previousImage.swipeRight(CurrentPhoto);
        }
        Toast.makeText(MainActivity.this, "next", Toast.LENGTH_SHORT).show();
    }

    public void SwipeLeft(){
        //put switch wallpaper method here
        CurrentPhoto = previousImage.swipeLeft();
        if(CurrentPhoto != null){
            if(lastSwipe == SwipeDirection.right){
                CurrentPhoto = previousImage.swipeLeft();
            }
            setBackgroundImage(CurrentPhoto);
        }
        Toast.makeText(MainActivity.this, "prev", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO:
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO:
    }

    @Override
    public void onConnected(Bundle bundle) {
        //TODO:
    }
}
