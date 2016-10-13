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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.logging.Logger;
import fragment.BlankFragment;
import fragment.LoginFragment;
import fragment.RegisterFragment;
import fragment.YoutubeFragment;
import utils.Config;




public class MainActivity extends AppCompatActivity implements
        BlankFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.toString();
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mSidebarList;




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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // necessary for session-cookie management in Volley
        CookieHandler.setDefault(new CookieManager());

        // init RequestQeueue object
        this.mRequestQueue = Volley.newRequestQueue(this);


        initToolbar();


        initSidebar();



        // instantiate login fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment loginFragment = new LoginFragment();
        fragmentManager.beginTransaction().replace(R.id.activity_main, loginFragment).commit();

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

                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragment != null) {
                    fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).commit();
                }
                else {
                    String video_id = "LHcP4MWABGY";
                    YoutubeFragment myFragment = YoutubeFragment.newInstance(video_id);
                    fragmentManager.beginTransaction().replace(R.id.activity_main, myFragment).commit();
                }

            }
        });

    }






    private void login() throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.72:4500/auth/login";
        String body = "{\"username\": \"ali\", \"password\": \"polipo\"}";
        JSONObject jsonBody = new JSONObject(body);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
                    tags();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());
                try {
                    tags();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        queue.add(request);
    }




    private void tags() throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.72:4500/api/update_tags";
//        String body = "{\"value\":\"aaa1\"}";

        JSONObject jsonBody = new JSONObject();

        ArrayList<String> list = new ArrayList<String>();
        list.add("chillout");
        list.add("electronica");
        jsonBody.put("value", new JSONArray(list));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
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


        queue.add(request);
    }








}
