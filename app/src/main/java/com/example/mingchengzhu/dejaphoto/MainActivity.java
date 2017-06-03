/*
 * MainActivity class
 * The primary activity for the app
 */

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
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * The primary activity for the app
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PhotoManagerClient,GoogleApiClient.OnConnectionFailedListener {

    // Used for logging
    private static final String TAG = "MainActivity";

    // Used with the photo chooser intent
    private static final int RESULT_LOAD_IMAGE = 1;
    GoogleApiClient mGoogleApiClient;
    // Used to receive an address result from FetchAddressIntentService
    private AddressResultReceiver resultReceiver;

    // Used for tracking system time and location
    private LocationManager locationManager;
    private LocationListener locationListener;
    private AutoSwitch autoSwitch;

    // true if we are currently showing the user a message about having no photos
    boolean noPhotosModeEnabled = false;

    private PopupWindow popup;

    // Field for setting panel
    public int refreshInterval = 180000; //1000 milliseconds = 1 seconds
    private final Handler autoSwitchHandler = new Handler();

    PhotoManager photoManager;
    iFirebase server;

    //Currently Signed-Users ID/email (request this using MainActivity.getCurrentUser() )
    public static String USER_ID;

    public static String getCurrentUser()
    {
        return USER_ID;
    }

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
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
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
                gotLocationText(photoManager.getCurrentPhoto(), text);
                Log.i(TAG, "location reverse geocoding succeeds");
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

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        photoManager = new PhotoManager(this);
        server = new RealFirebase();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /* The following code is used to implement swiping functionality */
        ImageView backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

        backgroundImage.setOnTouchListener(new OnSwipeListener(MainActivity.this) {

            @Override
            public void onSwipeRight() {
                // logging message
                Log.i(TAG, "user has swiped right");

                // call swipe right action & reset timer
                photoManager.next();
                autoSwitch.refresh();
                Log.i(TAG, "Timer is refreshed after user swipes right to get new photo");
            }
            @Override
            public void onSwipeTop() {
                // logging message
                Log.i(TAG, "user has swiped up");

                if (photoManager.getCurrentPhoto() != null) {
                    // set Karma and toast!
                    photoManager.getCurrentPhoto().setKarma(true);
                    Toast.makeText(MainActivity.this, "Karma !", Toast.LENGTH_SHORT).show();
                }
                //refresh timer
                autoSwitch.refresh();
                Log.i(TAG, "Timer is refreshed after user swipes up to add the Karma");
            }
            @Override
            public void onSwipeLeft() {
                // logging message
                Log.i(TAG, "user has swiped left");

                // call swipe left action & reset timer
                photoManager.prev();
                autoSwitch.refresh();
                Log.i(TAG, "Timer is refreshed after user swipes left to see an old photo");
            }
            @Override
            public void onSwipeDown() {
                // logging message
                Log.i(TAG, "user has swiped down");

                // Release and toast!
                if (photoManager.getCurrentPhoto() != null) {
                    photoManager.getCurrentPhoto().setReleased(true);
                    Toast.makeText(MainActivity.this, "Released !", Toast.LENGTH_SHORT).show();
                }
                // refresh timer
                autoSwitch.refresh();
                Log.i(TAG, "Timer is refreshed after user swipes down to release a photo");
            }
        });


        /* The following code is used to update screen based on location */
        locationListener = new LocationListener() {
            @Override
            // update tracker when location is changed
            public void onLocationChanged(Location location) {
                tracker.updateLocation(location);
                tracker.updateTime();
                Log.i(TAG, "System location and time is updated through tracker");
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
        // refresh time = 1 second, location update for every 500 meters
        locationManager.requestLocationUpdates(locationProvider, 1000, 500, locationListener);



        /* The following code is used to implement auto-switch */
        autoSwitch = new AutoSwitch(this, photoManager, autoSwitchHandler, refreshInterval);

        /* Start the runnable task*/
        autoSwitchHandler.postDelayed(autoSwitch, refreshInterval);


        /* The following code is used to get background when starts the app */
        photoManager.setCurrentPhoto(photoManager.getNextRandomImage());
        // if currentPhoto is null, it will display a message telling the user there are no photos
        setBackgroundImage(photoManager.getCurrentPhoto());

        // Get the email from the current google account
        Bundle extras = getIntent().getExtras();
        String email = extras.getString("email");


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
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

        if (id == R.id.nav_time) {
            if (photoManager.getMatchTime()) {
                item.setTitle("Time Off");
                photoManager.setMatchTime(false);
                Log.i(TAG, "Setting time off");
            } else {
                item.setTitle("Time On");
                photoManager.setMatchTime(true);
                Log.i(TAG, "Setting time on");
            }
        } else if (id == R.id.nav_date) {
            if (photoManager.getMatchDate()) {
                item.setTitle("Date Off");
                photoManager.setMatchDate(false);
                Log.i(TAG, "Setting date off");
            } else {
                item.setTitle("Date On");
                photoManager.setMatchDate(true);
                Log.i(TAG, "Setting date on");
            }
        } else if (id == R.id.nav_location) {
            if (photoManager.getMatchLocation()) {
                item.setTitle("Location Off");
                photoManager.setMatchLocation(false);
                Log.i(TAG, "Setting location off");
            } else {
                item.setTitle("Location On");
                photoManager.setMatchLocation(true);
                Log.i(TAG, "Setting location on");
            }
        } else if (id == R.id.nav_karma) {
            if (photoManager.getMatchKarma()) {
                item.setTitle("Karma Off");
                photoManager.setMatchKarma(false);
                Log.i(TAG, "Setting Karma off");
            } else {
                item.setTitle("Karma On");
                photoManager.setMatchKarma(true);
                Log.i(TAG, "Setting Karma on");
            }
        } else if (id == R.id.add_photo) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            AddPhoto();
        } else if(id == R.id.change_frequency) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            changeFrenquencyPopUp();
        } else if(id == R.id.setting){
            SettingsPopup();
        } else if(id == R.id.add_friend){
            add_friend();
        } else if(id == R.id.take_photo){
            take_photo();
        }
        return true;
    }

    public void add_friend(){
        LayoutInflater inflator2 = (LayoutInflater) getApplication().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)  inflator2.inflate(R.layout.add_friend_menu, null);

        popup = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relative_layout);
        popup.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        Button confirm = (Button) popup.getContentView().findViewById(R.id.add_friend_confirm);
        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                popup.dismiss();
            }
        });

        Button cancel = (Button) popup.getContentView().findViewById(R.id.add_friend_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
    }

    public void take_photo(){

    }
    
    public void SettingsPopup(){
        LayoutInflater inflator2 = (LayoutInflater) getApplication().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)  inflator2.inflate(R.layout.settings_menu, null);

        popup = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relative_layout);
        popup.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        final Button time_button = (Button) popup.getContentView().findViewById(R.id.nav_time_button);
        if(photoManager.getMatchTime()){
            time_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            time_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        time_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getMatchTime()) {
                    photoManager.setMatchTime(false);
                    Toast.makeText(MainActivity.this, "Time Off", Toast.LENGTH_SHORT).show();
                    time_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setMatchTime(true);
                    Toast.makeText(MainActivity.this, "Time On", Toast.LENGTH_SHORT).show();
                    time_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });

        final Button date_button = (Button) popup.getContentView().findViewById(R.id.nav_date_button);
        if(photoManager.getMatchDate()){
            date_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            date_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        date_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getMatchDate()) {
                    photoManager.setMatchDate(false);
                    Toast.makeText(MainActivity.this, "Date Off", Toast.LENGTH_SHORT).show();
                    date_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setMatchDate(true);
                    Toast.makeText(MainActivity.this, "Date On", Toast.LENGTH_SHORT).show();
                    date_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });

        final Button location_button = (Button) popup.getContentView().findViewById(R.id.nav_location_button);
        if(photoManager.getMatchLocation()){
            location_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            location_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        location_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getMatchLocation()) {
                    photoManager.setMatchLocation(false);
                    Toast.makeText(MainActivity.this, "Location Off", Toast.LENGTH_SHORT).show();
                    location_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setMatchLocation(true);
                    Toast.makeText(MainActivity.this, "Location On", Toast.LENGTH_SHORT).show();
                    location_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });

        final Button karma_button = (Button) popup.getContentView().findViewById(R.id.nav_karma_button);
        if(photoManager.getMatchKarma()){
            karma_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            karma_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        karma_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getMatchKarma()) {
                    photoManager.setMatchKarma(false);
                    Toast.makeText(MainActivity.this, "Karma Off", Toast.LENGTH_SHORT).show();
                    karma_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setMatchKarma(true);
                    Toast.makeText(MainActivity.this, "Karma On", Toast.LENGTH_SHORT).show();
                    karma_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });
        
        final Button myPhoto_button = (Button) popup.getContentView().findViewById(R.id.nav_my_photo_button);
        if(photoManager.getShowMine()){
            myPhoto_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            myPhoto_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        myPhoto_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getShowMine()) {
                    photoManager.setShowMine(false);
                    Toast.makeText(MainActivity.this, "Hide my Photos", Toast.LENGTH_SHORT).show();
                    myPhoto_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setShowMine(true);
                    Toast.makeText(MainActivity.this, "Show my Photos", Toast.LENGTH_SHORT).show();
                    myPhoto_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });

        final Button myFriend_button = (Button) popup.getContentView().findViewById(R.id.nav_friend_photo_button);
        if(photoManager.getShowFriends()){
            myFriend_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }else{
            myFriend_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        myFriend_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (photoManager.getShowFriends()) {
                    photoManager.setShowFriends(false);
                    Toast.makeText(MainActivity.this, "Hide Friends Photos", Toast.LENGTH_SHORT).show();
                    myFriend_button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }else{
                    photoManager.setShowFriends(true);
                    Toast.makeText(MainActivity.this, "Show Friends Photos", Toast.LENGTH_SHORT).show();
                    myFriend_button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);

                }
            }
        });


        final Button exit_button = (Button) popup.getContentView().findViewById(R.id.Setting_exit);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });

    }

    public void changeFrenquencyPopUp() {
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
                        refreshInterval = (Integer.valueOf(mEdit.getText().toString()))*1000;
                        if(autoSwitch != null) {
                            autoSwitch.setTime(refreshInterval);
                            autoSwitchHandler.removeCallbacks(autoSwitch);
                            autoSwitchHandler.postDelayed(autoSwitch, refreshInterval);
                        }
                        popup.dismiss();
                    }
                } catch(Exception e) {
                    popup.dismiss();
                    Log.d(TAG, "Overflow detected");
                }
            }
        });

        Button cancel = (Button) popup.getContentView().findViewById(R.id.frequency_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                popup.dismiss();
            }
        });
    }

    public void AddPhoto() {
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
                    photoManager.setCurrentPhoto(PhotoManager.addPhotoWithUri(selectedImage, this));
                    photoManager.getBackHistory().swipeRight(photoManager.getCurrentPhoto());

                    //Andy is Testing Writing to File
                    StateCodec.addDejaPhotoToSC(this, "stateCodec.txt", photoManager.getCurrentPhoto());

                    // Display the new photo immediately
                    setBackgroundImage(photoManager.getCurrentPhoto());

                    // reset timer
                    autoSwitch.refresh();

                    // Upload the photo
                    server.uploadDejaPhoto(photoManager.getCurrentPhoto(), new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.i(TAG, "Image uploaded successfully!");
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //TODO:
                        }
                    });
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
            if (resultReceiver == null) {
                resultReceiver = new AddressResultReceiver(new Handler());
            }

            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, resultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
            startService(intent);
        } else {
            gotLocationText(photo, "La La Land");
        }

        Log.i(TAG, "the app home screen should be set");
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

    public void currentPhotoChanged() {
        setBackgroundImage(photoManager.getCurrentPhoto());
    }

    private void signOut() {
        Log.i(TAG, "The account should be signed out");

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Log.i(TAG, "Should have returned to login screen");
                        // [END_EXCLUDE]
                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_button:
                if (mGoogleApiClient.isConnected()) {
                    signOut();
                }
                break;

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


}
