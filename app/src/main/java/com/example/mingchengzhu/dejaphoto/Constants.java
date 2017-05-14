package com.example.mingchengzhu.dejaphoto;

/**
 * Created by sterling on 5/12/17.
 * Description: Constants shared between classes app-wide
 */
public final class Constants {
    // Constants used to interact with FetchAddressIntentService:

    /**
     * Returned by FetchAddressIntentService when an address successfully found
     */
    public static final int FETCH_ADDRESS_SUCCESS = 0;

    /**
     * Returned by FetchAddressIntentService when it fails to find an address
     */
    public static final int FETCH_ADDRESS_FAILURE = 1;

    /**
     * Used in FetchAddressIntentService
     */
    public static final String PACKAGE_NAME = "com.example.mingchengzhu.dejaphoto";

    /**
     * Used in FetchAddressIntentService
     */
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    /**
     * Key to retrieve the resulting string from the bundle returned by FetchAddressIntentService
     */
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";

    /**
     * Key used with a Bundle to pass a Location object to FetchAddressIntentService
     */
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
}