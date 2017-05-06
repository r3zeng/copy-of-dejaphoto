package com.example.mingchengzhu.dejaphoto;
import java.util.Random;//needs to delete
import android.os.Handler;
import android.os.Bundle;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

import org.w3c.dom.Text;

/* imports that may be needed

package com.example.mingchengzhu.dejaphoto;
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
*/

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectionCallbacks, OnConnectionFailedListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation = new Location(""); //just for testing
    private Tracker tracker = new Tracker();
    private Runnable auto_switch;
    TextView textView;
    TextView textView2;
    
    private PopupWindow popup;
    
    private boolean Deja_Time = true;
    private boolean Deja_Date = true;
    private boolean Deja_Location = true;
    private boolean Deja_Karma = true;
    private int Deja_refresh_time = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLastLocation.setLongitude(0.0d);
        mLastLocation.setLatitude(0.0d);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        /* Task: auto-switch */
        final Handler auto_switch_handler = new Handler();
        auto_switch = new Runnable() {
            @Override
            public void run() {
                //call the change background method here

                //for testing
                Random random = new Random();
                textView = (TextView)findViewById(R.id.textView2);
                textView.setText(String.valueOf(random.nextInt(50)+1));
                textView2 = (TextView)findViewById(R.id.textView3);
                textView2.setText(String.valueOf(random.nextInt(50)+1));

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
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if ( mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        if (mLastLocation != null){
            Log.d("STATE", String.valueOf(mLastLocation.getLatitude()));
            Log.d("CREATION", String.valueOf(mLastLocation.getLongitude()));
        }
        // possible place to update location
        tracker.updateLocation(mLastLocation);
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
        Log.i(TAG, "connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
        Log.i(TAG, "connection failed");
    }

}
