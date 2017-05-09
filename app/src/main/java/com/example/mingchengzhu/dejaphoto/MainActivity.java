package com.example.mingchengzhu.dejaphoto;
import java.util.Random;//needs to delete

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.app.Service;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

import org.w3c.dom.Text;

// imports that may be needed

//package com.example.mingchengzhu.dejaphoto;
import java.util.Random;//needs to delete
import java.io.File;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

//*/

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectionCallbacks, OnConnectionFailedListener
{
    // Used for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    // Used with the photo chooser intent
    private static final int RESULT_LOAD_IMAGE = 1;

    private Tracker tracker = new Tracker();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Runnable auto_switch;
    TextView textView;
    TextView textView2;
    
    private PopupWindow popup;
    
    private boolean Deja_Time = true;
    private boolean Deja_Date = true;
    private boolean Deja_Location = true;
    private boolean Deja_Karma = true;
    private int Deja_refresh_time = 3000; //3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Swiping function */
        ImageView backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

        backgroundImage.setOnTouchListener(new OnSwipeListener(MainActivity.this){

            public void onSwipeRight(){
                //put switch wallpaper method here
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeTop() {
                //put addKarma method here
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                //put switch wallpaper method here
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeDown() {
                //put release method here
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
                /* test */
                textView = (TextView)findViewById(R.id.textView2);
                textView.setText(String.valueOf(tracker.getLocation().getLongitude()));
                textView2 = (TextView)findViewById(R.id.textView3);
                textView2.setText(String.valueOf(tracker.getTime()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        // specified refresh time, 500 meters
        locationManager.requestLocationUpdates(locationProvider, Deja_refresh_time, 500, locationListener);

        /* Task: auto-switch */
        final Handler auto_switch_handler = new Handler();
        auto_switch = new Runnable() {
            @Override
            public void run() {
                //call the change background method here
                /*
                //for testing
                Random random = new Random();
                textView = (TextView)findViewById(R.id.textView2);
                textView.setText(String.valueOf(random.nextInt(50)+1));
                textView2 = (TextView)findViewById(R.id.textView3);
                textView2.setText(String.valueOf(random.nextInt(50)+1));
                */
                /*
                textView = (TextView)findViewById(R.id.textView2);
                textView.setText(String.valueOf(mLastLocation.getLatitude()));
                textView2 = (TextView)findViewById(R.id.textView3);
                textView2.setText(String.valueOf(mLastLocation.getLongitude()));
                */
                auto_switch_handler.postDelayed(auto_switch, 3000);//3 minutes 180000

            }
        };

        /* Start the runnable task*/
        auto_switch_handler.post(auto_switch);

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
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

        popup = new PopupWindow(container, 500, 500, true);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relative_layout);
        popup.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, (width/2) -250, (height/2) -250);

        Button confirm = (Button) popup.getContentView().findViewById(R.id.frequency_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                  EditText mEdit = (EditText) popup.getContentView().findViewById(R.id.change_frequency_edittext);
                  try {//catch overflow
                      if (!mEdit.getText().toString().equals("")) {//no null strings
                          Deja_refresh_time = Integer.valueOf(mEdit.getText().toString());
                          popup.dismiss();
                      }
                  }catch(Exception e){
                      popup.dismiss();
                  }
            }
        });

        Button cancle = (Button) popup.getContentView().findViewById(R.id.frequency_cancle);
        cancle.setOnClickListener(new View.OnClickListener(){
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
            DejaPhoto photo = DejaPhoto.addPhotoWithUri(selectedImage, this);

            setBackgroundImage(photo.getUri());
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
        

        if(DejaPhoto.getCurrentSearchResults().length == 0){
            System.err.println("Error: getting next image from empty album");
            return null;
        }

        double largestWeight = 0;
        DejaPhoto selectedPhoto = null;

        for(int i = 0; i < DejaPhoto.getCurrentSearchResults().length; i++){
            DejaPhoto currentPhoto = DejaPhoto.getCurrentSearchResults()[i];
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
                * getDateWeight(photo) * getLocationWeight(photo);
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
        }else{
            long SystemTime = tracker.getTime();
            long PhotoTime = photo.getTime();

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_2_HOURS = 7200000;

            long difference = Math.abs(SystemTime - PhotoTime) % MILLISECONDS_IN_DAY;

            if(difference < MILLISECONDS_IN_2_HOURS){
                return 2;
            }else{
                return 0.5;
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
        }else{
            Location SystemLocation = tracker.getLocation();
            Location PhotoLocation = photo.getLocation();

            double DistanceInMeters = SystemLocation.distanceTo(PhotoLocation);

            if(DistanceInMeters < 200){
                return 2;
            }else{
                return 0.5;
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
                return 0.5;
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

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
        Log.i(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
        Log.i(TAG, "connection failed");
    }

}
