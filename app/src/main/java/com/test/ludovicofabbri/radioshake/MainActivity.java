package com.test.ludovicofabbri.radioshake;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;
import java.util.Stack;

import fragment.LoginFragment;
import fragment.RegisterFragment;
import fragment.TagsFragment;
import fragment.YoutubeControlsFragment;
import fragment.YoutubeFragment;
import utils.Config;
import utils.Utils;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        YoutubeControlsFragment.OnFragmentInteractionListener,
        TagsFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.toString();
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mSidebarList;
    private FragmentManager mFragmentManager;
    private Stack<Integer> mNavigationStack;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(LOG_TAG, location.toString());

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            sendCurrentLocation(latitude, longitude);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    /**
     *
     * @return the activity context
     */
    public Context getContext() {
        if (this.mContext == null) {
            this.mContext = this;
        }
        return this.mContext;
    }


    /**
     *
     * @return RequestQueue object
     */
    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(this);
        }
        return this.mRequestQueue;
    }


    /**
     * main FragmentManager
     * @return
     */
    public FragmentManager getMainFragmentManager() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        return this.mFragmentManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // necessary for session-cookie management in Volley
        CookieHandler.setDefault(new CookieManager());

        // init the navigation stack
        mNavigationStack = new Stack<Integer>();

        // init the FragmentManager
        this.mFragmentManager = getSupportFragmentManager();

        // init RequestQeueue object
        this.mRequestQueue = Volley.newRequestQueue(this);


        this.mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        askLocationPermissions();

        initToolbar();

        initSidebar();

        initButtons();


        // for debug: clear SharedPreferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = mLocationManager.getBestProvider(criteria, false);
            mLocationManager.requestLocationUpdates(provider, 10000, 0, mLocationListener);
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onFragmentBackToMain(String message) {

        Log.d(LOG_TAG, message);

        List<Fragment> fragmentList = mFragmentManager.getFragments();

        if (fragmentList == null) {
            return;
        }

        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                mFragmentManager.beginTransaction().remove(fragment).commit();
            }
        }

        // empty the backstack
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
            mFragmentManager.popBackStack();
        }

        findViewById(R.id.start_music_main).setVisibility(View.VISIBLE);
    }


    /**
     * initialize Toolbar
     */
    private void initToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_closed);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }


    /**
     * initialize Sidebar
     */
    private void initSidebar() {

        ArrayAdapter<String> mListAdapter;
        String fragmentArray[] = {"Home", "Login", "Register", "Youtube", "Tags", "Maps"};

        mSidebarList = (ListView) findViewById(R.id.sidebar_list);
        mListAdapter = new ArrayAdapter<String>(this, R.layout.sidebar_list_item_layout, R.id.sidebar_list_item, fragmentArray);
        mSidebarList.setAdapter(mListAdapter);

        mSidebarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int nextState = Config.NAV_MAIN_STATE;

                switch (i) {

                    // main
                    case 0:
                        onFragmentBackToMain("Back to main?");
                        mDrawerLayout.closeDrawers();
                        return;

                    // login
                    case 1:
                        nextState = Config.NAV_LOGIN_STATE;
                        break;

                    // register
                    case 2:
                        nextState = Config.NAV_REGISTER_STATE;
                        break;

                    // login
                    case 3:
                        nextState = Config.NAV_YOUTUBE_STATE;
                        break;

                    // login
                    case 4:
                        nextState = Config.NAV_TAGS_STATE;
                        break;

                    case 5:
                        nextState = Config.NAV_MAPS_STATE;
                        break;

                    default:
                        onFragmentBackToMain("Back to main?");
                        mDrawerLayout.closeDrawers();
                        return;

                }

                // navigate state
                navigationManager(nextState);

                // close navigation menu
                mDrawerLayout.closeDrawers();

            }
        });

    }


    /**
     * init buttons
     */
    private void initButtons() {

        Button startMusicButton = (Button) findViewById(R.id.start_music);
        startMusicButton.setClickable(true);

        startMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                btn.setClickable(false);
                navigationManager(Config.NAV_YOUTUBE_STATE);
            }
        });

    }


    /**
     * Navigation Manager
     * @param nextState
     */
    public void navigationManager(int nextState) {

        FragmentTransaction transaction = null;

        if (nextState != Config.NAV_MAIN_STATE) {
            findViewById(R.id.start_music_main).setVisibility(View.GONE);
        }


        switch (nextState) {

            case Config.NAV_LOGIN_STATE:
                transaction = mFragmentManager.beginTransaction().replace(R.id.activity_main, new LoginFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_REGISTER_STATE:
                transaction = mFragmentManager.beginTransaction().replace(R.id.activity_main, new RegisterFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_YOUTUBE_STATE:
                Fragment youtubeFragment = YoutubeFragment.newInstance();
                FragmentTransaction transaction1 = mFragmentManager.beginTransaction().replace(R.id.activity_main, youtubeFragment);
                FragmentTransaction transaction2 = mFragmentManager.beginTransaction().add(R.id.activity_main, new YoutubeControlsFragment());
                transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction1.addToBackStack(null).commit();
                transaction2.addToBackStack(null).commit();
                break;


            case Config.NAV_TAGS_STATE:
                transaction = mFragmentManager.beginTransaction().replace(R.id.activity_main, new TagsFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_MAPS_STATE:
                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                mapFragment.getMapAsync(this);
                transaction = mFragmentManager.beginTransaction().replace(R.id.activity_main, mapFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_SETTINGS_STATE:
                break;


            case Config.NAV_INFO_STATE:
                break;


            default:
//                findViewById(R.id.start_music_main).setVisibility(View.VISIBLE);
                break;
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.setIndoorEnabled(true);
            mGoogleMap.setBuildingsEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));




    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case Config.PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.



                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(LOG_TAG, "LOCATION PERMISSION GRANTED");

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                        String provider = mLocationManager.getBestProvider(criteria, false);
                        mLocationManager.requestLocationUpdates(provider, 10000, 0, mLocationListener);
                    }


                    } else {
                        // Permission was denied. Display an error message.
                        Log.e(LOG_TAG, "LOCATION PERMISSION DENIED");
                    }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    /**
     * askLocationPermissions at startup
     */
    private void askLocationPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Config.PERMISSION_REQUEST_LOCATION);

    }



    /**
     * sendCurrentLocation
     */
    public void sendCurrentLocation(double latitude, double longitude) {

        String jsonString = "{\"value\" : {\"latitude\":" + latitude + ", \"longitude\":" + longitude + "}}";
        JSONObject body = null;
        try {
            body = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_UPDATE_POSITION_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
                    Utils.createOkToast(getContext(), response.getString("value"), 2000);   // delete me
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());
            }
        });

        mRequestQueue.add(request);

    }









}
