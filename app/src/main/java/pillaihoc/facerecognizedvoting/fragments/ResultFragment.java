package pillaihoc.facerecognizedvoting.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;
import pillaihoc.facerecognizedvoting.pojo.VotingEventStruct;

/**
 * Created by deepakgavkar on 20/03/17.
 */
public class ResultFragment extends BaseFragment {

    @Bind(R.id.spEvents)
    Spinner spEvents;
    @Bind(R.id.tvWin)
    TextView tvWin;
    List<VotingEventStruct> list = new ArrayList<VotingEventStruct>();
    private SweetAlertDialog sweetAlertDialog;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);
        requestQueue = Volley.newRequestQueue(getActivity());
        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            grabAllEvents();
        } else {
            Utils.ShowShortToast(getActivity(), Constants.NOINTERNET);
        }

        spEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    getWinner(getEventID(list.get(i).getEventname()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    public void show(String title, String content) {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(content);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    public void dismissDialog() {
        try {
            if (sweetAlertDialog.isShowing()) {
                sweetAlertDialog.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEventID(String eventName) {
        String id = "";
        for (int i = 0; i < list.size(); i++) {
            if (eventName.equals(list.get(i).getEventname())) {
                id = list.get(i).getId();
            }
        }
        return id;
    }

    private void getWinner(String id) {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");
        hashMap.put("eventid", id);

        show("Getting data", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.GETRESULTS, hashMap, this.SUCCESS(), this.FAIl());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.GETRESULTS + "" + hashMap);
        requestQueue.add(jsObjRequest);
    }


    private Response.ErrorListener FAIl() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> SUCCESS() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                dismissDialog();
                try {
                    if (response.has("data")) {
                        String name = response.getString("data");
                        JSONArray arr = new JSONArray(name);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject post = arr.optJSONObject(i);

                            tvWin.setText(post.optString("vName") + " have won the election of " + post.optString("eventName") + " with total votes " + post.optString("totalcount"));
                        }
                    }

                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "No data found!");
                }
            }
        };
        return listener;
    }


    private void grabAllEvents() {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");

        show("Getting data", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.GETALLEVENT, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.GETALLEVENT + "" + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                dismissDialog();
                try {
                    if (response.has("data")) {
                        String name = response.getString("data");
                        JSONArray arr = new JSONArray(name);
                        list.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject post = arr.optJSONObject(i);
                            VotingEventStruct votingEventStruct = new VotingEventStruct();

                            votingEventStruct.setId(post.optString("id"));
                            votingEventStruct.setEventname(post.optString("eventName"));

                            list.add(votingEventStruct);
                        }
                        ArrayAdapter<VotingEventStruct> type = new ArrayAdapter<VotingEventStruct>(getActivity(), R.layout.spinner_item, list);
                        type.setDropDownViewResource(R.layout.spinner_dropdown);
                        spEvents.setAdapter(type);
                    } else if (response.has("fail")) {
                        Utils.ShowShortToast(getActivity(), response.getString("fail"));
                    }

                } catch (JSONException e) {
                    dismissDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "No volunteers found!");
                }
            }
        };
        return listener;
    }
}
