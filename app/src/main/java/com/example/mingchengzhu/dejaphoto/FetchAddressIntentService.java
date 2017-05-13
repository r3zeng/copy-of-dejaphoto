package com.example.mingchengzhu.dejaphoto;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;

    private static final String TAG = "FetchAddress";

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "FetchAddressIntentService now processing an intent");

        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "service not available", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid coordinates.";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FETCH_ADDRESS_FAILURE, errorMessage);
        } else {
            Log.i(TAG, "Found the address");

            for (Address address : addresses) {
                String featureName = address.getFeatureName();
                if (featureName == null || featureName.length() == 0) {
                    continue;
                }

                boolean onlySpaceAndNumbers = true;
                for (int i = 0; i < featureName.length(); ++i) {
                    char c = featureName.charAt(i);
                    if ((c < '0' || c > '9') && c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                        onlySpaceAndNumbers = false;
                    }
                }

                if (!onlySpaceAndNumbers) {
                    deliverResultToReceiver(Constants.FETCH_ADDRESS_SUCCESS, featureName);
                    return;
                }
            }

            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            int maxIndex = address.getMaxAddressLineIndex();
            for(int i = 0; i <= maxIndex; i++) {
                // Don't put "USA" in the address
                String line = address.getAddressLine(i);
                if (! line.equalsIgnoreCase("USA") || maxIndex <= 1) {
                    addressFragments.add(line);
                }
            }
            deliverResultToReceiver(Constants.FETCH_ADDRESS_SUCCESS,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);

        Log.d(TAG, "delivering result: " + message);

        if (mReceiver != null) {
            mReceiver.send(resultCode, bundle);
        }
    }
}
