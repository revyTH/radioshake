package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import utils.Config;
import utils.Utils;


public class YoutubeFragment extends YouTubePlayerSupportFragment {

    private static final String LOG_TAG = YoutubeFragment.class.toString();

    private String currentVideoID = "video_id";
    private static YouTubePlayer activePlayer;


    /**
     * newInstance
     *
     * @return
     */
    public static YoutubeFragment newInstance(Bundle bundle) {

        YoutubeFragment playerYouTubeFrag = new YoutubeFragment();

        if (bundle != null) {
            playerYouTubeFrag.init(bundle);
        }

        else {
            playerYouTubeFrag.init(null);
        }



        return playerYouTubeFrag;
    }



    /**
     *
     * @return YoutubeFragment player
     */
    public static YouTubePlayer getActivePlayer() {

        return activePlayer;
    }




    private void init(final Bundle bundle) {

        initialize(Config.YOUTUBE_ANDROID_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {


                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                activePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);




//                if (!wasRestored) {

                    // do something?
                    FragmentManager fragmentManager = ((MainActivity)getActivity()).getMainFragmentManager();
                    final YoutubeControlsFragment youtubeControlsFragment = new YoutubeControlsFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction().add(R.id.activity_main, youtubeControlsFragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commitNow(); // addToBackStack + commitNow = IllegalSateException?


                    // init youtube controls
                    youtubeControlsFragment.initControls();



                    activePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {

                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {
                            Log.d(LOG_TAG, "Youtube video ended: loadNextTrack called");

                            // increment the track index in SharedPreferences
                            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                            int index = sharedPreferences.getInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0) + 1;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, index);
                            editor.commit();

                            youtubeControlsFragment.loadNextTrack();
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {

                        }
                    });



                    if (bundle != null) {
                        String trackID = bundle.getString(Config.TRACK_ID);
                        String artistName = bundle.getString(Config.ARTIST_NAME);
                        String songTitle = bundle.getString(Config.SONG_TITLE);

                        youtubeControlsFragment.loadYoutubeVideo(trackID, artistName, songTitle);
                    }
                    else {
                        youtubeControlsFragment.loadNextTrack();
                    }



                // }


                activePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener(){
                    @Override
                    public void onFullscreen(boolean arg0) {
                        // do full screen stuff here, or don't. I started a YouTubeStandalonePlayer
                        // to go to full screen
                        Log.d(LOG_TAG, "Youtube player full screen mode: disabled");
                    }});
            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        return super.onCreateView(layoutInflater, viewGroup, bundle);

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams)getView().getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;;

        RelativeLayout container = (RelativeLayout) getActivity().findViewById(R.id.activity_main);
        int height = container.getMeasuredHeight();
        Log.d(LOG_TAG, "LAYOUT HEIGHT = " + height);

        layoutParams.height = height - 150;
        getView().setLayoutParams(layoutParams);
    }






}
