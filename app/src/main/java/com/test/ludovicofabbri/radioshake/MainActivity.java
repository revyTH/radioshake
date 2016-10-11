package com.test.ludovicofabbri.radioshake;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.test.ludovicofabbri.radioshake.R;

import fragment.BlankFragment;
import fragment.YoutubeFragment;


public class MainActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener{

    private Toolbar myToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    ListView sidebar_list;
    ArrayAdapter<String> listAdapter;
    String fragmentArray[] = {"Frag1", "Frag2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // action bar init
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, myToolbar, R.string.drawer_open, R.string.drawer_closed);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();



        // navigation menu init
        sidebar_list = (ListView) findViewById(R.id.sidebar_list);
        listAdapter = new ArrayAdapter<String>(this, R.layout.sidebar_list_item_layout, R.id.sidebar_list_item, fragmentArray);
        sidebar_list.setAdapter(listAdapter);

        sidebar_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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


}
