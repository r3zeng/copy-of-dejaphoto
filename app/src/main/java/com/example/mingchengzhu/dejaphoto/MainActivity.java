package com.example.mingchengzhu.dejaphoto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    public Tracker tracker = new Tracker();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private AutoSwitch auto_switch;

    // true if we are currently showing the user a message about having no photos
    boolean noPhotosModeEnabled = false;

    private PopupWindow popup;
    // Field for setting panel
    public boolean Deja_Time = true;
    public boolean Deja_Date = true;
    public boolean Deja_Location = true;
    public boolean Deja_Karma = true;
    public int Deja_refresh_time = 10000; //10 seconds
    private final Handler auto_switch_handler = new Handler();
    PreviousImage previousImage;
    DejaPhoto CurrentPhoto;

    WeightAlgo algo;

    /**
     * turns on/off a message about having no photos
     *
     * @param enabled true if we should be showing a message about having no photos
     */
    void setNoPhotosModeEnabled(boolean enabled) {
        noPhotosModeEnabled = enabled;

        ImageView background = (ImageView) findViewById(R.id.backgroundImage);

        if (enabled) {
            Log.i(TAG, "enabling no photos mode");

            int resID = getResources().getIdentifier("default_background", "drawable",  getPackageName());
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(resID) + '/' +
                    getResources().getResourceTypeName(resID) + '/' +
                    getResources().getResourceEntryName(resID) );
            background.setImageURI(uri);

        } else {
            Log.i(TAG, "disabling no photos mode");

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
                String text = resultData.getString(Constants.RESULT_DATA_KEY);
                gotLocationText(CurrentPhoto, text);
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

        algo = new WeightAlgo(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /* The following code is used to implement swiping functionality */
        ImageView backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

        backgroundImage.setOnTouchListener(new OnSwipeListener(MainActivity.this){

            @Override
            public void onSwipeRight(){
                // logging message
                Log.i(TAG, "user has swiped right");

                // call swipe right action & reset timer
                algo.next();
                auto_switch.refresh();
            }
            @Override
            public void onSwipeTop() {
                // logging message
                Log.i(TAG, "user has swiped up");

                if(CurrentPhoto != null){
                    // set Karma and toast!
                    CurrentPhoto.setKarma(true);
                    Toast.makeText(MainActivity.this, "Karma !", Toast.LENGTH_SHORT).show();
                }
                //refresh timer
                auto_switch.refresh();
            }
            @Override
            public void onSwipeLeft() {
                // logging message
                Log.i(TAG, "user has swiped left");

                // call swipe left action & reset timer
                algo.prev();
                auto_switch.refresh();
            }
            @Override
            public void onSwipeDown() {
                // logging message
                Log.i(TAG, "user has swiped down");

                // Release and toast!
                if(CurrentPhoto != null){
                    CurrentPhoto.setReleased(true);
                    Toast.makeText(MainActivity.this, "Released !", Toast.LENGTH_SHORT).show();
                }
                // refresh timer
                auto_switch.refresh();
            }
        });


        /* The following code is used to update screen based on location */
        locationListener = new LocationListener() {
            @Override
            // update tracker when location is changed
            public void onLocationChanged(Location location) {
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
        // refresh time = Deja_refresh_time, location update for every 500 meters
        locationManager.requestLocationUpdates(locationProvider, Deja_refresh_time, 500, locationListener);



        /* The following code is used to implement auto-switch */
        auto_switch = new AutoSwitch(this, auto_switch_handler, Deja_refresh_time);

        /* Start the runnable task*/
        auto_switch_handler.postDelayed(auto_switch, Deja_refresh_time);


        /* The following code is used to get background when starts the app */
        CurrentPhoto = algo.getNextRandomImage();
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
        Log.i(TAG, "user pressed the back button");

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
        return super.onOptionsItemSelected(item);
    }

    /* The following code is used to implement the navigation bar */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // logging message
        Log.i(TAG, "User selected navigation item " + id);

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

        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
            {
                if (resultCode == RESULT_OK && null != data) {
                    Log.i(TAG, "user selected an image to add");

                    Uri selectedImage = data.getData();
                    CurrentPhoto = DejaPhoto.addPhotoWithUri(selectedImage, this);
                    previousImage.swipeRight(CurrentPhoto);

                    //Andy is Testing Writing to File
                    StateCodec.addDejaPhotoToSC(this, "stateCodec.txt", CurrentPhoto);

                    // Display the new photo immediately
                    setBackgroundImage(CurrentPhoto);

                    // reset timer
                    auto_switch.refresh();
                }
                break;
            }
            default:
            {
                Log.w(TAG, "onActivityResult got unknown requestCode: " + requestCode);
                break;
            }
        }
    }

    /* Setting wallpaper method*/
    void setWallpaper(final DejaPhoto photo, final String locationText) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Uri uri = photo.getUri();
                try {
                    Log.i(TAG, "attempting to set wallpaper");
                    InputStream image_stream = getContentResolver().openInputStream(uri);
                    final BitmapFactory.Options inOptions = new BitmapFactory.Options();
                    inOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(image_stream, null, inOptions);
                    final int originalWidth = inOptions.outWidth;
                    final int originalHeight = inOptions.outHeight;

                    image_stream.close();
                    image_stream = getContentResolver().openInputStream(uri);

                    Point screenSize = new Point();
                    getWindowManager().getDefaultDisplay().getRealSize(screenSize);

                    int screenWidth = screenSize.x;
                    int screenHeight = screenSize.y;
                    if (screenWidth > screenHeight) {
                        int tmp = screenWidth;
                        screenWidth = screenHeight;
                        screenHeight = tmp;
                    }

                    int scaleFactor = Math.max(1, Math.min(originalWidth / screenWidth, originalHeight / screenHeight));

                    BitmapFactory.Options decodeBitmapOptions = new BitmapFactory.Options();
                    decodeBitmapOptions.inSampleSize = scaleFactor;

                    Bitmap bitmap = BitmapFactory.decodeStream(image_stream, null, decodeBitmapOptions);
                    if (bitmap == null) {
                        Log.e(TAG, "could not decode bitmap while attempting to set wallpaper");
                        return;
                    }

                    float screenRatio = (float)screenHeight / screenWidth;
                    float bitmapRatio = (float)bitmap.getHeight() / bitmap.getWidth();
                    if (bitmapRatio > screenRatio) {
                        int newHeight = (int)(bitmap.getWidth() * screenRatio);
                        bitmap = bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - newHeight) / 2, bitmap.getWidth(), newHeight);
                    } else {
                        int newWidth = (int)(bitmap.getHeight() / screenRatio);
                        bitmap = bitmap.createBitmap(bitmap, (bitmap.getWidth() - newWidth) / 2, 0, newWidth, bitmap.getHeight());
                    }

                    bitmap = bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);

                    if (locationText != null && locationText.length() > 0) {
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(screenWidth / 18);

                        paint.setShadowLayer(40, 0, 0, Color.BLACK);

                        String[] lines = locationText.split("\n");
                        final float x = 40.0f;
                        final float yStart = screenHeight - 80.0f;
                        float y = yStart;
                        for (int i = lines.length - 1; i >= 0; --i) {
                            y -= paint.descent() - paint.ascent();
                            canvas.drawText(lines[i], x, y, paint);
                        }
                    }

                    // setting wallpaper with the converted bitmap
                    WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    myWallpaperManager.setWallpaperOffsetSteps(1, 1);
                    myWallpaperManager.suggestDesiredDimensions(bitmap.getWidth(), bitmap.getHeight());
                    myWallpaperManager.setBitmap(bitmap);

                    Log.i(TAG, "wallpaper should now be set, new dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight() +
                            " made for screen size: " + screenWidth + "x" + screenHeight);
                }
                catch (FileNotFoundException e) {
                    Log.e(TAG, "file not found while trying to set wallpaper", e);
                }
                catch (IOException e) {
                    Log.e(TAG, "IO error while trying to set wallpaper", e);
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
        } else {
            gotLocationText(photo, "La La Land");
        }
    }

    /**
     * This method is called when the location text has become available for a photo
     *
     * @param photo the photo that we now have location text for
     * @param locationText the location text
     */
    void gotLocationText(DejaPhoto photo, String locationText) {
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationTextView.setText(locationText);

        setWallpaper(photo, locationText);
    }

}
