package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.test.ludovicofabbri.radioshake.MainActivity;
import com.test.ludovicofabbri.radioshake.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import utils.Config;
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






    private void initTags() {

        RequestQueue requestQueue = ((MainActivity)getActivity()).getRequestQueue();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.PY_SERVER_TAGS_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Log.d(LOG_TAG, response.toString(4));

                    for (int i = 0; i < response.length(); i++) {
                        String s = (String)response.get(i);
                        mTagsList.add(s);
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
            }
        });

        requestQueue.add(request);

    }




    private void initUI() {

        if (mTagsList == null) {
            return;
        }
        
        ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();

        for (int i = 0; i < mTagsList.size(); i++) {

            if (i != 0 && i % 2 == 0) {

                RelativeLayout relativeLayout = new RelativeLayout(getActivity());

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                relativeLayout.setLayoutParams(layoutParams);


                RelativeLayout.LayoutParams checkbox_relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                checkbox_relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                relativeLayout.addView(checkBoxes.get(0), checkbox_relativeParams);

                checkbox_relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                checkbox_relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                if (checkBoxes.size() > 1) {
                    relativeLayout.addView(checkBoxes.get(1), checkbox_relativeParams);

                    LinearLayout tagsList = (LinearLayout)getActivity().findViewById(R.id.tags_list);
                    tagsList.addView(relativeLayout);
                }



                checkBoxes.clear();

            }

            else {
                CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setText(mTagsList.get(i));
                checkBox.setPadding(16, 16, 16, 16);
                checkBox.setTextColor(getResources().getColor(R.color.white));

                checkBoxes.add(checkBox);
            }





        }

    }


}
