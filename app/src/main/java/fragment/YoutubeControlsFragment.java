package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        //initialize youtube controls
        initControls();
    }










    /**
     * initialize control buttons
     */
    private void initControls() {

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
     */
    private void playControlHandler(final ImageButton control) {


        // first check if there are already recommendations in SharedPreferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String jsonString = sharedPref.getString(Config.SHARED_PREF_LAST_RECOMMENDATIONS, null);
        if (jsonString != null) {
            int index = sharedPref.getInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0);
            try {
                JSONArray recommendedTracks = new JSONArray(jsonString);
                JSONObject nextTrack = (JSONObject) recommendedTracks.get(index);
                String artistName = nextTrack.getString(Config.ARTIST_NAME);
                String songTitle = nextTrack.getString(Config.SONG_TITLE);
                loadYoutubeVideo(artistName, songTitle);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            // re-enable control
            control.setClickable(true);
            return;
        }





        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

//        if (!activePlayer.isPlaying()) {
//            activePlayer.play();
//        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.PY_SERVER_NEXT_TRACKS_URL, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {


                try {
                    Log.d(LOG_TAG, response.toString(4));
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.SHARED_PREF_LAST_RECOMMENDATIONS, response.toString());
                    editor.putInt(Config.SHARED_PREF_LAST_RECOMMENDATIONS_INDEX, 0);
                    editor.commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // re-enable ImageButton control
                control.setClickable(true);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    int statusCode = error.networkResponse.statusCode;
                    JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    String message = jsonResponse.getString("value");
                    Utils.createErrorToast(getActivity(), message, 3000).show();
                    Log.e(LOG_TAG, message);

                    if (statusCode == Config.HTTP_STATUS_CODE_UNAUTHORIZED) {
                        FragmentManager fragmentManager = ((MainActivity)getActivity()).getMainFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.activity_main, new LoginFragment()).addToBackStack(null).commit();
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Whoops! An error occurred.", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Whoops! An error occurred.", Toast.LENGTH_LONG).show();
                }

                // re-enable ImageButton control
                control.setClickable(true);
            }

        });


        mRequestQueue.add(request);

    }






    private void pauseControlHandler(final ImageButton control) {
        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        if (activePlayer.isPlaying()) {
            activePlayer.pause();
        }
    }

    private void forwardControlHandler(final ImageButton control) {

    }

    private void dislikeControlHandler(final ImageButton control) {

    }




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





















    public void loadYoutubeVideo(String artistName, String songTitle) {

        final YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        String query = null;
        try {
            query = URLEncoder.encode(artistName + " " + songTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

//        String url = Config.YOUTUBE_QUERY_URL + "?key=" + Config.YOUTUBE_API_KEY + "&part=snippet&q=" + query + "&type=video&videoEmbeddable=true";
        String url = Config.YOUTUBE_QUERY_URL + "?key=" + Config.YOUTUBE_API_KEY + "&part=snippet&q=" + query + "&type=video";



        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.w(LOG_TAG, response.names().toString(4));
                            JSONArray items = (JSONArray)response.get("items");
                            JSONObject firstResult = items.getJSONObject(0);
                            Log.w(LOG_TAG, firstResult.toString(4));
                            String videoId = firstResult.getJSONObject("id").getString("videoId");
                            Log.w(LOG_TAG, videoId);
                            activePlayer.loadVideo(videoId, 0);

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


        // Add the request to the RequestQueue.
        mRequestQueue.add(request);

    }






}
