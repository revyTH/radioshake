package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import utils.Config;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String LOG_TAG = SettingsFragment.class.toString();

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
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
            mListener.onFragmentBackToMain("Backward navigation SETTINGS");
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

        Switch sharePositionSwitch = (Switch)getActivity().findViewById(R.id.share_position_switch);

        sharePositionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                compoundButton.setClickable(false);
                setSharePosition(b, compoundButton);

            }
        });

    }














    /**
     * setSharePosition
     * @param flag
     */
    public void setSharePosition(final boolean flag, final CompoundButton control) {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

       final SharedPreferences sharedPref = getActivity().getPreferences(MODE_PRIVATE);

        if (!sharedPref.contains(Config.SHARED_PREF_SHARE_POSITION) || sharedPref.getBoolean(Config.SHARED_PREF_SHARE_POSITION, false) != flag) {

            String jsonString = "{\"value\": " + flag + "}";
            JSONObject body = null;
            try {
                body = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_SET_SHARE_POSITION_URL, body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(LOG_TAG, response.toString(4));
                        Utils.createOkToast(getContext(), response.getString("value"), 3000).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(Config.SHARED_PREF_SHARE_POSITION, flag);
                    editor.commit();

                    control.setClickable(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, error.toString());
                    try {
                        JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        String message = jsonResponse.getString("value");
                        Utils.createErrorToast(getContext(), message, 3000).show();
                        Log.e(LOG_TAG, message);
                    }
                    catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    control.setChecked(false);
                    control.setClickable(true);


                }
            });

            requestQueue.add(request);
        }

    }








}
