package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import utils.Config;
import utils.MyJsonObjectRequest;
import utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String LOG_TAG = RegisterFragment.class.toString();
    private RequestQueue mRequestQueue;


    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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

        this.mRequestQueue = ((MainActivity)getActivity()).getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
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

        // handle backward navigation
        if (isRemoving()) {
            mListener.onFragmentBackToMain("Backward navigation REGISTER");
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
        void onFragmentInteraction(Uri uri);
        void onFragmentBackToMain(String message);
    }






    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // attach event listeners on buttons
        initButtons();
    }



    /**
     * init buttons listeners
     */
    private void initButtons() {

        Button registerButton = (Button)getActivity().findViewById(R.id.register_button);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Register button pressed");

                try {
                    register();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * validate register form
     * @param username
     * @param password
     * @param repeatPassword
     * @return
     */
    private boolean validateForm(String username, String password, String repeatPassword) {

        if (username.length() < 3) {
            String errorMessage = "Username must be at least 3 characters";
            Utils.createErrorToast(getActivity(), errorMessage, Config.TOAST_DURATION).show();
            return false;
        }

        if (password.length() < 6) {
            String errorMessage = "Password must be at least 6 characters";
            Utils.createErrorToast(getActivity(), errorMessage, Config.TOAST_DURATION).show();
            return false;
        }

        if (!password.equals(repeatPassword)) {
            String errorMessage = "Password fields don't match";
            Utils.createErrorToast(getActivity(), errorMessage, Config.TOAST_DURATION).show();
            return false;
        }

        return true;
    }



    /**
     * Register new user
     * @throws JSONException
     */
    private void register() throws JSONException {

        EditText usernameEditText = (EditText)getActivity().findViewById(R.id.username);
        String username = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText)getActivity().findViewById(R.id.password);
        String password = passwordEditText.getText().toString();

        EditText repeatPasswordEditText = (EditText)getActivity().findViewById(R.id.repeat_password);
        String repeatPassword = repeatPasswordEditText.getText().toString();


        boolean validate = validateForm(username, password, repeatPassword);
        if (!validate) {
            return;
        }


        String body = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        JSONObject jsonBody = new JSONObject(body);



        MyJsonObjectRequest request = new MyJsonObjectRequest(Request.Method.POST, Config.PY_SERVER_REGISTER_URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(LOG_TAG, response.toString(4));
                    String message = response.getString("value");
                    Utils.createOkToast(getActivity(), message, 3000).show();
                    ((MainActivity)getActivity()).navigationManager(Config.NAV_LOGIN_STATE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(LOG_TAG, error.toString());

                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    String message = jsonResponse.getString("value");
                    Utils.createErrorToast(getActivity(), message, 3000).show();
                    Log.e(LOG_TAG, message);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Whoops! An error occurred", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Whoops! An error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });

        // add request to queue
        this.mRequestQueue.add(request);
    }



}
