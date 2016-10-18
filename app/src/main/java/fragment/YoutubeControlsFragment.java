package fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubePlayer;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import utils.Config;
import utils.MyJsonObjectRequest;
import utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YoutubeControlsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoutubeControlsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoutubeControlsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String LOG_TAG = YoutubeControlsFragment.class.toString();
    private RequestQueue mRequestQueue;

    private OnFragmentInteractionListener mListener;

    public YoutubeControlsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoutubeControlsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubeControlsFragment newInstance(String param1, String param2) {
        YoutubeControlsFragment fragment = new YoutubeControlsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // get RequestQeue from the MainActivity
        this.mRequestQueue = ((MainActivity)getActivity()).getRequestQueue();

        // restore start_music button clickable
        Button startMusicBtn = (Button)((MainActivity)getActivity()).findViewById(R.id.start_music);
        startMusicBtn.setClickable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube_controls, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();


        // handle backward navigation
        if (isRemoving()) {
            mListener.onFragmentBackToMain("Backward navigation YOUTUBE");
        }

        mListener = null;



        // stop sending location
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean isAlreadySendingPosition = sharedPref.getBoolean(Config.SHARED_PREF_ALREADY_SENDING_POSITION, false);

        if (!isAlreadySendingPosition) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                LocationManager locationManager = ((MainActivity)getActivity()).getLocationManager();
                LocationListener locationListener = ((MainActivity)getActivity()).getLocationListener();

                locationManager.removeUpdates(locationListener);

                editor.putBoolean(Config.SHARED_PREF_ALREADY_SENDING_POSITION, false);
                editor.commit();
            }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentBackToMain(String message);
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Initialization must be done in YoutubeFragment onInitializationSuccess, when the player is read

        //initialize youtube controls
        // initControls();

        // loadNextTrack();
    }










    /**
     * initialize control buttons
     */
    public void initControls() {

        final ImageButton playControl = (ImageButton) getView().findViewById(R.id.play_control);
        final ImageButton pauseControl = (ImageButton) getActivity().findViewById(R.id.pause_control);
        final ImageButton forwardControl = (ImageButton) getActivity().findViewById(R.id.forward_control);
        final ImageButton dislikeControl = (ImageButton) getActivity().findViewById(R.id.dislike_control);
        final ImageButton infoControl = (ImageButton) getActivity().findViewById(R.id.info_control);


        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Play control pressed");
                playControl.setClickable(false);
                playControlHandler(playControl);
            }
        });

        pauseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Pause control pressed");
                pauseControl.setClickable(false);
                pauseControlHandler(pauseControl);
            }
        });

        forwardControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Forward control pressed");
                forwardControl.setClickable(false);
                forwardControlHandler(forwardControl);
            }
        });

        dislikeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Dislike control pressed");
                dislikeControl.setClickable(false);
                dislikeControlHandler(dislikeControl);
            }
        });

        infoControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Info control pressed");
                infoControl.setClickable(false);
                infoControlHandler(infoControl);
            }
        });

    }


    /**
     * playControlHandler
     * @param control
     */
    private void playControlHandler(final ImageButton control) {

        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        if (!activePlayer.isPlaying()) {
            try {
                activePlayer.play();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        // re-enable control
        control.setClickable(true);

    }


    /**
     * pauseControlHandler
     * @param control
     */
    private void pauseControlHandler(final ImageButton control) {

        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        if (activePlayer.isPlaying()) {
            try {
                activePlayer.pause();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }

        // re-enable control
        control.setClickable(true);
    }


    /**
     * forwardControlHandler
     * @param control
     */
    private void forwardControlHandler(final ImageButton control) {

        // increment the track index in SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int index = sharedPreferences.getInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0) + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, index);
        editor.commit();

        loadNextTrack();

        // re-enable control
        control.setClickable(true);

    }

    /**
     * dislikeControlHandler
     * @param control
     */
    private void dislikeControlHandler(final ImageButton control) {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String trackID = sharedPref.getString(Config.SHARED_PREF_LAST_CURRENT_TRACK, null);

        if (trackID == null) {
            Utils.createErrorToast(getActivity(), "No track loaded", 3000).show();
            return;
        }

        String jsonString = String.format("{\"value\":\"%s\"}", trackID);
        JSONObject body = null;
        try {
            body = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_UPDATE_DISLIKES_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
                    Utils.createOkToast(getActivity(), response.getString("value"), 3000).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());

                try {
                    JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    String message = jsonResponse.getString("value");
                    Utils.createErrorToast(getActivity(), message, 3000).show();
                    Log.e(LOG_TAG, message);
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        // send request
        requestQueue.add(request);

        // re-enable control
        control.setClickable(true);
    }


    /**
     * infoControlHandler
     * @param control
     */
    private void infoControlHandler(final ImageButton control) {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("LAST_RECOMMENDATIONS", null);

        try {
            JSONArray lastRecommendations = new JSONArray(jsonString);
            Log.d(LOG_TAG, lastRecommendations.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // re-enable ImageButton control
        control.setClickable(true);

    }




    /**
     * loadNextTrack
     */
    public void loadNextTrack() {

        try {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            String jsonArrayString = sharedPreferences.getString(Config.SHARED_PREF_LAST_RECOMMENDATIONS, null);

            // if we have already some recommended tracks
            if (jsonArrayString != null) {
                int index = sharedPreferences.getInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0);
                JSONArray tracks = new JSONArray(jsonArrayString);

                // if index is in range, we load the track
                if (index < tracks.length()) {
                    JSONObject track = (JSONObject)tracks.get(index);
                    String trackID = track.getString(Config.TRACK_ID);
                    String artistName = track.getString(Config.ARTIST_NAME);
                    String songTitle = track.getString(Config.SONG_TITLE);
                    loadYoutubeVideo(trackID, artistName, songTitle);
                    return;
                }
                // otherwise get next track(s) from server
                else {
                    getNextTracksFromServer();
                    return;
                }
            }
            // otherwise get next track(s) from server
            else {
                getNextTracksFromServer();
                return;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }






    /**
     * getNextTracksFromServer
     */
    private void getNextTracksFromServer() {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Config.PY_SERVER_NEXT_TRACKS_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d(LOG_TAG, response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    JSONArray tracks = (JSONArray)response.get("value");
                    if (tracks.length() > 0) {
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(Config.SHARED_PREF_LAST_RECOMMENDATIONS, tracks.toString());
                        editor.putInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0);
                        editor.commit();

                        loadNextTrack();
                        return;
                    }
                    else {
                        getNextTracksFromServer();
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(LOG_TAG, error.toString());

                if (error.networkResponse.statusCode == Config.HTTP_STATUS_CODE_UNAUTHORIZED) {
                    Utils.createErrorToast(getActivity(), "You are not logged in: please sign in", 3000).show();
                    ((MainActivity)getActivity()).navigationManager(Config.NAV_LOGIN_STATE, null);
                }

            }
        });

        requestQueue.add(request);

    }





    /**
     * loadYoutubeVideo
     * @param artistName
     * @param songTitle
     */
    public void loadYoutubeVideo(final String trackID, final String artistName, final String songTitle) {

        final YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        String query = null;
        try {
            query = URLEncoder.encode(artistName + " " + songTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = Config.YOUTUBE_QUERY_URL + "?key=" + Config.YOUTUBE_API_KEY + "&part=snippet&q=" + query + "&type=video&videoEmbeddable=true";
//        String url = Config.YOUTUBE_QUERY_URL + "?key=" + Config.YOUTUBE_API_KEY + "&part=snippet&q=" + query + "&type=video";



        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.w(LOG_TAG, response.names().toString(4));
                            JSONArray items = (JSONArray)response.get("items");

                            // if there are no videos, get next track
                            if (items.length() == 0) {
                                loadNextTrack();
                                return;
                            }

                            JSONObject firstResult = items.getJSONObject(0);
                            String videoId = firstResult.getJSONObject("id").getString("videoId");
                            try {
                                activePlayer.loadVideo(videoId, 0);
                                Utils.createOkToast(getActivity(), artistName + " : " + songTitle, 3000).show();

                                // add current playing track to preferences
                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(Config.SHARED_PREF_LAST_CURRENT_TRACK, trackID);
                                editor.commit();

                                // send current track as listened to the server
                                updateListenedTracks(trackID);

                                // set current_track on the server
                                setCurrentTrack(trackID, artistName, songTitle);

                                // if share_position is true, send periodically current position during playback
                                boolean sharePosition = sharedPref.getBoolean(Config.SHARED_PREF_SHARE_POSITION, false);
                                boolean isAlreadySendingPosition = sharedPref.getBoolean(Config.SHARED_PREF_ALREADY_SENDING_POSITION, false);
                                if (sharePosition && !isAlreadySendingPosition) {
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                                        Criteria criteria = new Criteria();
                                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                                        LocationManager locationManager = ((MainActivity)getActivity()).getLocationManager();
                                        LocationListener locationListener = ((MainActivity)getActivity()).getLocationListener();
                                        String provider = locationManager.getBestProvider(criteria, false);
                                        locationManager.requestLocationUpdates(provider, Config.SEND_POSITION_INTERVAL_MS, Config.SEND_POSITION_DISTANCE_M, locationListener);

                                        editor.putBoolean(Config.SHARED_PREF_ALREADY_SENDING_POSITION, true);
                                        editor.commit();
                                    }
                                }


                            }
                            catch (IllegalStateException e) {
                                Log.e(LOG_TAG, e.toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.toString());

                        if (error.networkResponse.statusCode == Config.HTTP_STATUS_CODE_UNAUTHORIZED) {
                            Utils.createErrorToast(getActivity(), "You are not logged in: random track selected", 3000).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        mRequestQueue.add(request);
    }


    /**
     * updateListenedTracks
     * @param trackID
     */
    private void updateListenedTracks(String trackID) {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        String jsonString = String.format("{\"value\":\"%s\"}", trackID);
        JSONObject body = null;
        try {
            body = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (body == null) {
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_UPDATE_LISTENED_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
//                    Utils.createOkToast(getActivity(), response.getString("value"), 3000).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());

                try {
                    JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    String message = jsonResponse.getString("value");
//                    Utils.createErrorToast(getActivity(), message, 3000).show();
                    Log.e(LOG_TAG, message);
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // send request
        requestQueue.add(request);

    }





    private void setCurrentTrack(String trackID, String artistName, String songTitle) {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        String jsonString = "{\"value\": {";
        jsonString += String.format("\"trackid\" : \"%s\",", trackID);
        jsonString += String.format("\"artist_name\" : \"%s\",", artistName);
        jsonString += String.format("\"song_title\" : \"%s\"", songTitle);
        jsonString += "}}";

        JSONObject body = null;
        try {
            body = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_SET_CURRENT_TRACK, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d(LOG_TAG, response.toString(4));
//                    Utils.createOkToast(getActivity(), response.getString("value"), 3000).show();
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

        requestQueue.add(request);

    }




}
