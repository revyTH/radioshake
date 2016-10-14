package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.youtube.player.YouTubePlayer;
import com.test.ludovicofabbri.radioshake.R;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube_controls, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
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

        ImageButton playControl = (ImageButton) getView().findViewById(R.id.play_control);
        ImageButton pauseControl = (ImageButton) getActivity().findViewById(R.id.pause_control);
        ImageButton forwardControl = (ImageButton) getActivity().findViewById(R.id.forward_control);
        ImageButton dislikeControl = (ImageButton) getActivity().findViewById(R.id.dislike_control);
        ImageButton infoControl = (ImageButton) getActivity().findViewById(R.id.info_control);


        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Play control pressed");
                playControlHandler();
            }
        });

        pauseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Pause control pressed");
                pauseControlHandler();
            }
        });

        forwardControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Forward control pressed");
                forwardControlHandler();
            }
        });

        dislikeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Dislike control pressed");
                dislikeControlHandler();
            }
        });

        infoControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Info control pressed");
                infoControlHandler();
            }
        });

    }


    /**
     * playControlHandler
     */
    private void playControlHandler() {

        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        if (!activePlayer.isPlaying()) {
            activePlayer.play();
        }
    }


    private void pauseControlHandler() {
        YouTubePlayer activePlayer = YoutubeFragment.getActivePlayer();

        if (activePlayer == null) {
            return;
        }

        if (activePlayer.isPlaying()) {
            activePlayer.pause();
        }
    }

    private void forwardControlHandler() {

    }

    private void dislikeControlHandler() {

    }

    private void infoControlHandler() {

    }


}
