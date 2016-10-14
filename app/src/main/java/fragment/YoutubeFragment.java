package fragment;

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

    public static YoutubeFragment newInstance(String url) {

        YoutubeFragment playerYouTubeFrag = new YoutubeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        playerYouTubeFrag.init(); //This line right here

        return playerYouTubeFrag;
    }

    /**
     *
     * @return YoutubeFragment instance
     */
    public static YouTubePlayer getActivePlayer() {
        return activePlayer;
    }

    private void init() {

        initialize(Config.YOUTUBE_ANDROID_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {


                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                activePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

                if (!wasRestored) {

//                    activePlayer.loadVideo(getArguments().getString("url"), 0);

//                    activePlayer.cueVideo(getArguments().getString("url"), 20000);

                    try {
                        testVolley();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }


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












    public void testVolley() throws UnsupportedEncodingException {

        RequestQueue queue = Volley.newRequestQueue((MainActivity)getActivity());

        String query = URLEncoder.encode("Blue Stahli Ultranumb", "UTF-8");
        String url = Config.YOUTUBE_QUERY_URL + "?key=" + Config.YOUTUBE_API_KEY + "&part=snippet&q=" + query + "&type=video&videoEmbeddable=true";



        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

//                        Log.w(LOG_TAG, response.names().toString(4));

                            JSONArray items = (JSONArray)response.get("items");
                            JSONObject firstResult = items.getJSONObject(0);
                            Log.w(LOG_TAG, firstResult.toString(4));
                            String videoId = firstResult.getJSONObject("id").getString("videoId");
                            Log.w(LOG_TAG, videoId);
                            activePlayer.loadVideo(videoId, 0);


//                        Log.w(LOG_TAG, response.toString(4));
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



        url = "http://192.168.1.72:4500/api/tags";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
//                    Log.w(LOG_TAG, response.toString());
                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Volley error.");
                    }
                });





        // Add the request to the RequestQueue.
        queue.add(jsonObjRequest);
//        queue.add(jsonArrayRequest);

    }
}
