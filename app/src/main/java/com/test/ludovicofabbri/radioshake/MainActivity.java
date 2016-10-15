package com.test.ludovicofabbri.radioshake;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.test.ludovicofabbri.radioshake.R;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import fragment.BlankFragment;
import fragment.DeleteFragment;
import fragment.LoginFragment;
import fragment.RegisterFragment;
import fragment.YoutubeControlsFragment;
import fragment.YoutubeFragment;
import utils.Config;




public class MainActivity extends AppCompatActivity implements
        BlankFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        YoutubeControlsFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.toString();
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mSidebarList;
    private FragmentManager mFragmentManager;
    private Stack<Integer> mNavigationStack;




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


        initToolbar();


        initSidebar();



        // instantiate login fragment
        navigationManager(Config.NAV_LOGIN_STATE, false);

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
        String fragmentArray[] = {"Frag1", "Frag2"};

        mSidebarList = (ListView) findViewById(R.id.sidebar_list);
        mListAdapter = new ArrayAdapter<String>(this, R.layout.sidebar_list_item_layout, R.id.sidebar_list_item, fragmentArray);
        mSidebarList.setAdapter(mListAdapter);

        mSidebarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fragment fragment;

                switch (i) {

                    case 0:
                        fragment = new BlankFragment();
                        break;

                    case 1:
                        fragment = null;
                        break;

                    default:
                        fragment = null;
                        break;
                }



                if (fragment != null) {
                    mFragmentManager.beginTransaction().replace(R.id.activity_main, fragment).commit();
                }
                else {
//                    String video_id = "LHcP4MWABGY";
//                    YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(video_id);


//                    mFragmentManager.beginTransaction().replace(R.id.activity_main, youtubeFragment).addToBackStack(null).commit();
//                    mFragmentManager.beginTransaction().add(R.id.activity_main, new YoutubeControlsFragment()).addToBackStack(null).commit();

                    navigationManager(Config.NAV_YOUTUBE_STATE, false);
                }

                // close navigation menu
                mDrawerLayout.closeDrawers();

            }
        });

    }


    /**
     * Handle fragments navigation in the main activity
     * @param nextState
     */
    public void navigationManager(int nextState, boolean isBackwardNavigation) {

        if (mNavigationStack == null) {
            mNavigationStack = new Stack<Integer>();
        }

        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        // if is backward navigation we extract the previous state from the stack
        if (isBackwardNavigation) {

            if (mNavigationStack.empty()) {
                nextState = Config.NAV_DEFAULT_STATE;
            }

            else {
                // pop current state
                mNavigationStack.pop();

                // check again if stack empty
                if (mNavigationStack.empty()) {
                    nextState = Config.NAV_DEFAULT_STATE;
                }

                // else we get the previous state in the stack
                else {
                    nextState = mNavigationStack.pop();
                }
            }
        }
        // otherwise we push the next state in the stack
        else {
            mNavigationStack.push(nextState);
        }


        switch (nextState) {

            case Config.NAV_MAIN_STATE:
                break;


            case Config.NAV_LOGIN_STATE:
                mFragmentManager.beginTransaction().replace(R.id.activity_main, new LoginFragment()).addToBackStack(null).commit();
                break;


            case Config.NAV_REGISTER_STATE:
                mFragmentManager.beginTransaction().replace(R.id.activity_main, new RegisterFragment()).addToBackStack(null).commit();
                break;


            case Config.NAV_YOUTUBE_STATE:
                Fragment youtubeFragment = YoutubeFragment.newInstance();
                mFragmentManager.beginTransaction().replace(R.id.activity_main, youtubeFragment).commit();
                // add only one to the backstack
                mFragmentManager.beginTransaction().add(R.id.activity_main, new YoutubeControlsFragment()).addToBackStack(null).commit();
                break;


            case Config.NAV_TAGS_STATE:
                break;


            case Config.NAV_MAPS_STATE:
                break;


            case Config.NAV_SETTINGS_STATE:
                break;


            case Config.NAV_INFO_STATE:
                break;


            default:
                mFragmentManager.beginTransaction().replace(R.id.activity_main, new LoginFragment()).commit();
                break;
        }

    }












}
