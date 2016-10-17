package fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.Config;
import utils.MyJsonObjectRequest;
import utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String LOG_TAG = TagsFragment.class.toString();
    private ArrayList<String> mTagsList;
    private Map<String, Boolean> mTagsChosen;


    private OnFragmentInteractionListener mListener;

    public TagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsFragment newInstance(String param1, String param2) {
        TagsFragment fragment = new TagsFragment();
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

        mTagsList = new ArrayList<String>();
        mTagsChosen = new HashMap<String, Boolean>();

        initTags();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tags, container, false);
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

        if (isRemoving()) {
            mListener.onFragmentBackToMain("Back to main from TAGS");
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

        // initialize button(s)
        initButtons();
    }

    private void initTags() {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        JsonArrayRequest tagsRequest = new JsonArrayRequest(Request.Method.GET, Config.PY_SERVER_TAGS_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Log.d(LOG_TAG, response.toString(4));

                    for (int i = 0; i < response.length(); i++) {
                        String s = (String)response.get(i);
                        mTagsList.add(s);
                        mTagsChosen.put(s, false);
                    }

                    initUserTags();

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

        requestQueue.add(tagsRequest);

    }



    private void initUserTags() {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        JsonObjectRequest userTagsRequest = new JsonObjectRequest(Request.Method.GET, Config.PY_SERVER_USER_TAGS_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d(LOG_TAG, response.toString(4));

                    JSONArray jsonArray = (JSONArray) response.get("value");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String tag = (String)jsonArray.get(i);
                        mTagsChosen.put(tag, true);
                    }

                    initUI();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());
                // init UI in any case
                initUI();
            }
        });

        requestQueue.add(userTagsRequest);
    }




    private void initUI() {

        if (mTagsList == null) {
            return;
        }


        for (int i = 0; i < mTagsList.size(); i++) {

            // container for checkboxes
            RelativeLayout layout = new RelativeLayout(getActivity());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layout.setPadding(16, 16, 16, 16);
            layout.setLayoutParams(params);



            // left checkbox
//            CheckBox checkBoxLeft = new CheckBox(getActivity());
            AppCompatCheckBox checkBoxLeft = new AppCompatCheckBox(getActivity());
            setCheckBoxColor(checkBoxLeft, getResources().getColor(R.color.white), getResources().getColor(R.color.white));
            checkBoxLeft.setPadding(16, 16, 16, 16);
            checkBoxLeft.setText(mTagsList.get(i));
            if (mTagsChosen.get(mTagsList.get(i))) {
                checkBoxLeft.setChecked(true);
            }
            checkBoxLeft.setTextSize(18f);
            checkBoxLeft.setTextColor(getResources().getColor(R.color.white));
            RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
            checkBoxLeft.setLayoutParams(leftParams);

            // add event listener for leftCheckbox
            checkBoxLeft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String tag = compoundButton.getText().toString();


                    if (compoundButton.isChecked()) {
                        mTagsChosen.put(tag, true);
                        utils.Utils.createOkToast(getActivity(), "Checked " + tag, 3000).show();
                    }
                    else {
                        mTagsChosen.put(tag, false);
                        utils.Utils.createOkToast(getActivity(), "Unchecked " + tag, 3000).show();
                    }
                }
            });


            i = i + 1;

            if (i == mTagsList.size()) {
                layout.addView(checkBoxLeft);
                LinearLayout tagsList = (LinearLayout)getActivity().findViewById(R.id.tags_list);
                tagsList.addView(layout);
                return;
            }

            // right checkbox
//            CheckBox checkBoxRight = new CheckBox(getActivity());
            AppCompatCheckBox checkBoxRight = new AppCompatCheckBox(getActivity());
            setCheckBoxColor(checkBoxRight, getResources().getColor(R.color.white), getResources().getColor(R.color.white));
            checkBoxRight.setPadding(16, 16, 16, 16);
            checkBoxRight.setText(mTagsList.get(i));
            if (mTagsChosen.get(mTagsList.get(i))) {
                checkBoxRight.setChecked(true);
            }
            checkBoxRight.setTextSize(18f);
            checkBoxRight.setTextColor(getResources().getColor(R.color.white));
            RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            checkBoxRight.setLayoutParams(rightParams);


            // add event listener for rightCheckbox
            checkBoxRight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String tag = compoundButton.getText().toString();


                    if (compoundButton.isChecked()) {
                        mTagsChosen.put(tag, true);
                        utils.Utils.createOkToast(getActivity(), "Checked " + tag, 3000).show();
                    }
                    else {
                        mTagsChosen.put(tag, false);
                        utils.Utils.createOkToast(getActivity(), "Unchecked " + tag, 3000).show();
                    }
                }
            });

            layout.addView(checkBoxLeft);
            layout.addView(checkBoxRight);
            LinearLayout tagsList = (LinearLayout)getActivity().findViewById(R.id.tags_list);
            tagsList.addView(layout);


        }

    }



    private void initButtons() {

        Button setTagsButton = (Button)getActivity().findViewById(R.id.set_tags_btn);

        setTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Button button = (Button)view;
                button.setClickable(false);

                final RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();


                ArrayList<String> tagsChosenList = new ArrayList<String>();
                Iterator it = mTagsChosen.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<String, Boolean> pair = (Map.Entry<String, Boolean>)it.next();
                    if (pair.getValue()) {
                        tagsChosenList.add(pair.getKey());
                    }
                }


                if (tagsChosenList.size() == 0) {
                    Utils.createErrorToast(getActivity(), "Please select at least one music tag", 4000).show();
                    button.setClickable(true);
                    return;
                }


                JSONObject body = new JSONObject();
                JSONArray bodyValue = new JSONArray(tagsChosenList);
                try {
                    body.put("value", bodyValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.PY_SERVER_UPDATE_TAGS_URL, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(LOG_TAG, response.toString(4));
                            String message = response.getString("value");
                            Utils.createOkToast(getActivity(), message, 3000).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        button.setClickable(true);
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
                            Utils.createErrorToast(getActivity(), "Whoops, an error occurred", 3000).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utils.createErrorToast(getActivity(), "Whoops, an error occurred", 3000).show();
                        }


                        if (error.networkResponse.statusCode == Config.HTTP_STATUS_CODE_UNAUTHORIZED) {
                            Utils.createErrorToast(getActivity(), "You are not logged in: please login", 3000).show();
                            android.support.v4.app.FragmentManager manager = ((MainActivity)getActivity()).getMainFragmentManager();
                            manager.beginTransaction().replace(R.id.activity_main, new LoginFragment()).addToBackStack(null).commit();

                        }

                        button.setClickable(true);
                    }
                });

                requestQueue.add(request);

            }
        });

    }



    private static void setCheckBoxColor(AppCompatCheckBox checkBox, int uncheckedColor, int checkedColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        uncheckedColor,
                        checkedColor
                }
        );
        checkBox.setSupportButtonTintList(colorStateList);
    }


}
