package com.test.ludovicofabbri.radioshake;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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




public class MainActivity extends AppCompatActivity implements
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
        String fragmentArray[] = {"Home", "Login", "Register", "Youtube", "Tags"};

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
                transaction =  mFragmentManager.beginTransaction().replace(R.id.activity_main, new LoginFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_REGISTER_STATE:
                transaction =  mFragmentManager.beginTransaction().replace(R.id.activity_main, new RegisterFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_YOUTUBE_STATE:
                Fragment youtubeFragment = YoutubeFragment.newInstance();
                FragmentTransaction transaction1 =  mFragmentManager.beginTransaction().replace(R.id.activity_main, youtubeFragment);
                FragmentTransaction transaction2 =  mFragmentManager.beginTransaction().add(R.id.activity_main, new YoutubeControlsFragment());
                transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction1.addToBackStack(null).commit();
                transaction2.addToBackStack(null).commit();
                break;


            case Config.NAV_TAGS_STATE:
                transaction =  mFragmentManager.beginTransaction().replace(R.id.activity_main, new TagsFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null).commit();
                break;


            case Config.NAV_MAPS_STATE:
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












}
