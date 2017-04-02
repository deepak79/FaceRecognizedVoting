package pillaihoc.facerecognizedvoting.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import pillaihoc.facerecognizedvoting.adapters.RecyclerAdapterVolunteers;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;
import pillaihoc.facerecognizedvoting.pojo.Volunteers;

/**
 * Created by deepakgavkar on 09/09/16.
 */
public class AllVolunteersFragment extends BaseFragment {

    @Bind(R.id.RecyclerViewVolunteers)
    RecyclerView RecyclerViewVolunteers;

    List<Volunteers> ListVolunteers = new ArrayList<Volunteers>();
    private SweetAlertDialog sweetAlertDialog;
    private RecyclerAdapterVolunteers recyclerAdapterVolunteers;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_volunteers_fragment, container, false);
        ButterKnife.bind(this, view);
        requestQueue = Volley.newRequestQueue(getActivity());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewVolunteers.setLayoutManager(llm);
        try {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("All Volunteers");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            grabAllVolunteers();
        } else {
            Utils.ShowShortToast(getActivity(), Constants.NOINTERNET);
        }

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

    private void grabAllVolunteers() {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");
        hashMap.put("flag", "1");
        hashMap.put("ward", "0");

        show("Getting data", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.GETALLVOLUNTEERS, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.GETALLVOLUNTEERS + "" + hashMap);
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

                try {
                    if(response.has("data")){
                        String name = response.getString("data");
                        JSONArray arr = new JSONArray(name);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject post = arr.optJSONObject(i);
                            Volunteers volunteers = new Volunteers();

                            volunteers.setId(post.optString("id"));
                            volunteers.setName(post.optString("name"));
                            volunteers.setPartyName(post.optString("partyName"));
                            volunteers.setLogo(post.optString("logo"));
                            volunteers.setIsActive(post.optString("flag"));
                            volunteers.setWard(post.optString("ward"));
                            volunteers.setFlag(post.optString("volunteer"));

                            ListVolunteers.add(volunteers);
                        }
                        dismissDialog();

                        if (ListVolunteers.size() != 0) {
                            recyclerAdapterVolunteers = new RecyclerAdapterVolunteers(getActivity(), ListVolunteers);
                            RecyclerViewVolunteers.setAdapter(recyclerAdapterVolunteers);
                            recyclerAdapterVolunteers.notifyDataSetChanged();
                        } else {
                            Utils.ShowShortToast(getActivity(), "No volunteers found!");
                        }
                    }else if(response.has("fail")) {
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
