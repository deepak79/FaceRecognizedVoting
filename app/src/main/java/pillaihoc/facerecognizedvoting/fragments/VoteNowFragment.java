package pillaihoc.facerecognizedvoting.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.enums.SerialNumber;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;
import pillaihoc.facerecognizedvoting.pojo.Volunteers;

/**
 * Created by deepakgavkar on 09/09/16.
 */
public class VoteNowFragment extends BaseFragment {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "FaceRecognition";
    private final static int IMAGE_MAX_HEIGHT = 600, IMAGE_MAX_WIDTH = 800;
    @Bind(R.id.spWard)
    Spinner spWard;
    @Bind(R.id.spVolunteers)
    Spinner spVolunteers;
    @Bind(R.id.btnVoteNow)
    Button btnVoteNow;
    @Bind(R.id.tvLeaderInformation)
    TextView tvLeaderInformation;
    @Bind(R.id.tvEventName)
    TextView tvEventName;
    ArrayList<String> listWards = new ArrayList<String>();
    String id = "";
    ArrayList<Volunteers> ListVolunteers = new ArrayList<Volunteers>();
    Kairos kairos;
    KairosListener listener;
    ArrayList<String> datas = new ArrayList<String>();
    private Uri fileUri;
    private SweetAlertDialog sweetAlertDialog;
    private RequestQueue requestQueue;

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vote_now_fragment, container, false);
        ButterKnife.bind(this, view);
        requestQueue = Volley.newRequestQueue(getActivity());
        setAdapter();
        datas = getAllDetails(getActivity());
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Vote Now");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            checkStatus();
        } else {
            Utils.ShowShortToast(getActivity(), Constants.NOINTERNET);
        }

        listener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                dismissDialog();
                Utils.PrintErrorLog("SUCCESS", "" + s);
                try {
//                    {
//                    "images": [{
//                        "transaction": {
//                            "status": "success",
//                                    "topLeftX": 50,
//                                    "topLeftY": 66,
//                                    "gallery_name": "test",
//                                    "subject_id": "49152892prabhat",
//                                    "confidence": 0.78076,
//                                    "height": 72,
//                                    "width": 72,
//                                    "face_id": 1,
//                                    "quality": 0.74741
//                        },
//                        "candidates": [{
//                            "subject_id": "49152892prabhat",
//                                    "confidence": 0.78076,
//                                    "enrollment_timestamp": "1489733679845"
//                        }]
//                    }]
//                }
                    JSONObject root = new JSONObject(s);
                    JSONArray images = root.getJSONArray("images");
                    JSONObject imageRoot = images.getJSONObject(0);
                    String transaction = imageRoot.getString("transaction");
                    JSONObject trans = new JSONObject(transaction);
                    String status = trans.getString("status");
                    String subid = trans.getString("subject_id");

                    JSONArray candidates = imageRoot.getJSONArray("candidates");
                    JSONObject rootCan = candidates.getJSONObject(0);
                    String subjectid = rootCan.getString("subject_id");


                    if (s.contains("success")) {
                        if (datas.get(SerialNumber.LISTSUBJECTID).equals(subid) && datas.get(SerialNumber.LISTSUBJECTID).equals(subjectid)) {
                            voteNow();
                        } else {
                            Utils.ShowShortToast(getActivity(), "Face not matched with user!");
                        }
                    } else {
                        JSONObject rootObject = new JSONObject(s);
                        JSONArray Errors = rootObject.getJSONArray("Errors");
                        JSONObject data = Errors.getJSONObject(0);
                        String Message = data.getString("Message");
                        Utils.ShowShortToast(getActivity(), Message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String s) {
                dismissDialog();
                Utils.PrintErrorLog("FAIL", "" + s);
            }
        };

        return view;
    }

    @OnClick(R.id.btnVoteNow)
    public void onVote() {
        if (validate() == true) {
            AskDialog("Confirm", "Do you really want to make vote to " + spVolunteers.getSelectedItem().toString() + " of " + pullVolunteerPartyName(spVolunteers.getSelectedItem().toString()) + " political party?");
        }
    }

    public void AskDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isDeviceSupportCamera()) {
                            captureImage();
                        } else {
                            Utils.ShowLongToast(getActivity(), "Your device doesn't support camera!");
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public boolean validate() {
        if (spWard.getSelectedItem().equals("Select")) {
            Utils.ShowShortToast(getActivity(), "Please select your ward");
            return false;
        }
        if (spVolunteers.getSelectedItem().equals("Select")) {
            Utils.ShowShortToast(getActivity(), "Please select volunteer you want to make vote");
            return false;
        }
        return true;
    }

    public void setAdapter() {
        if (listWards.size() > 0) {
            listWards.clear();
        }

        listWards.add("Select");
        listWards.add("WARD A");
        listWards.add("WARD B");
        listWards.add("WARD C");
        listWards.add("WARD D");
        listWards.add("WARD E");
        listWards.add("WARD F NORTH");
        listWards.add("WARD F SOUTH");
        listWards.add("WARD G NORTH");
        listWards.add("WARD G SOUTH");
        listWards.add("WARD H EAST");
        listWards.add("WARD H WEST");
        listWards.add("WARD K EAST");
        listWards.add("WARD K WEST");
        listWards.add("WARD P NORTH");
        listWards.add("WARD P SOUTH");
        listWards.add("WARD R CENTRAL");
        listWards.add("WARD R NORTH");
        listWards.add("WARD R SOUTH");
        listWards.add("WARD L");
        listWards.add("WARD M EAST");
        listWards.add("WARD M WEST");
        listWards.add("WARD N ");
        listWards.add("WARD S");

        findUserWard();
    }

    public void findUserWard() {

        datas = getAllDetails(getActivity());
        String userWard = datas.get(SerialNumber.LISTWARD);

        ArrayList<String> ward = new ArrayList<String>();
        ward.add("Select");
        ward.add(userWard);
        ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, ward);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spWard.setAdapter(type);
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

    private void checkStatus() {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");

        show("Checking Event Status", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.EVENTSTATUS, hashMap, this.createRequestSuccessListener2(), this.createRequestErrorListener2());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.EVENTSTATUS + "" + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener2() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Utils.ShowShortToast(getActivity(), "Please try again!");
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> createRequestSuccessListener2() {
        dismissDialog();
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("active")) {
                        JSONArray active = response.getJSONArray("active");
                        JSONObject jsonObject = active.getJSONObject(0);
                        id = "";
                        id = jsonObject.getString("id");
                        tvEventName.setText(jsonObject.getString("eventName"));
                    } else if (response.has("passive")) {
                        Utils.ShowShortToast(getActivity(), "No political event is live now!");
                        showMyPofile();
                    } else if (response.has("fail")) {
                        Utils.ShowShortToast(getActivity(), response.getString("fail"));
                        showMyPofile();
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "Please try again!");
                }
            }
        };
        return listener;
    }

    private void voteNow() {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");
        hashMap.put("eventid", id);
        hashMap.put("userid", datas.get(SerialNumber.LISTID));
        hashMap.put("id", pullVolunteerID(spVolunteers.getSelectedItem().toString()));

        show("Requesting", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.VOTENOW, hashMap, this.createRequestSuccessListenerVoteNow(), this.createRequestErrorListenerVoteNow());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.VOTENOW + "" + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListenerVoteNow() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Utils.ShowShortToast(getActivity(), "Unable to process your request!");
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> createRequestSuccessListenerVoteNow() {
        dismissDialog();
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("pass")) {
                        Utils.ShowShortToast(getActivity(), response.getString("pass"));
                        showMyPofile();
                    } else if (response.has("fail")) {
                        Utils.ShowShortToast(getActivity(), response.getString("fail"));
                    }
                } catch (JSONException e) {
                    dismissDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), Constants.ISSUE);
                }
            }
        };
        return listener;
    }

    private void grabVolunteerByWard(String ward) {

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("admin", "admin");
        hashMap.put("flag", "0");
        hashMap.put("ward", ward);

        show("Getting data", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.GETALLVOLUNTEERS, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Params", "URL" + Constants.BASE_URL + Constants.GETALLVOLUNTEERS + "" + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Utils.ShowShortToast(getActivity(), "Unable to process your request!");
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        dismissDialog();
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("data")) {
                        String name = response.getString("data");
                        JSONArray arr = new JSONArray(name);
                        ListVolunteers.clear();
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
                            setThings();
                        } else {
                            initializeVolunteer();
                            tvLeaderInformation.setText("There's no volunteer in your ward, You can't vote!");
                        }
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

    public void setThings() {
        ArrayList<String> volunteersName = new ArrayList<String>();
        volunteersName.add("Select");
        for (int i = 0; i < ListVolunteers.size(); i++) {
            volunteersName.add(ListVolunteers.get(i).getName());
        }
        setNameAdapter(volunteersName);
    }

    public void setNameAdapter(ArrayList<String> list) {
        ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spVolunteers.setAdapter(type);
    }

    public String pullVolunteerPartyName(String volunteerName) {
        String partyName = "";
        for (int i = 0; i < ListVolunteers.size(); i++) {
            if (ListVolunteers.get(i).getName().equals(volunteerName)) {
                partyName = ListVolunteers.get(i).getPartyName();
            }
        }
        return partyName;
    }

    public String pullVolunteerID(String volunteerName) {
        String id = "";
        for (int i = 0; i < ListVolunteers.size(); i++) {
            if (ListVolunteers.get(i).getName().equals(volunteerName)) {
                id = ListVolunteers.get(i).getId();
            }
        }
        return id;
    }

    @OnItemSelected(R.id.spVolunteers)
    public void onSelect() {
        String volunteer = spVolunteers.getSelectedItem().toString();
        String partyName = pullVolunteerPartyName(volunteer);
        int position = spVolunteers.getSelectedItemPosition();
        if (position != 0) {
            tvLeaderInformation.setText(volunteer + " is volunteer of " + partyName + " political party");
            btnVoteNow.setText("Vote " + volunteer + " now");
        } else {
            if (spWard.getSelectedItem().toString().equals("Select")) {
                tvLeaderInformation.setText("Please select your ward");
                btnVoteNow.setText("Vote now");
            } else if (ListVolunteers.size() > 0) {
                tvLeaderInformation.setText("Please select volunteer you want to vote for");
                btnVoteNow.setText("Vote now");
            }
        }
    }

    public void initializeVolunteer() {
        ListVolunteers.clear();
        ArrayList<String> volunteersName = new ArrayList<String>();
        volunteersName.add("Select");
        ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, volunteersName);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spVolunteers.setAdapter(type);
    }

    @OnItemSelected(R.id.spWard)
    public void onWardSelected() {
        String wardName = spWard.getSelectedItem().toString();
        int position = spWard.getSelectedItemPosition();
        if (position != 0) {
            grabVolunteerByWard(wardName);
        } else {
            initializeVolunteer();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Utils.ShowLongToast(getActivity(), "User cancelled image capture");
            } else {
                Utils.ShowLongToast(getActivity(), "Sorry! Failed to capture image");
            }
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            try {
                kairos = new Kairos();

                kairos.setAuthentication(getActivity(), Constants.APP_ID, Constants.APP_KEY);
                String galleryId = Constants.GALLERYID;
                kairos.recognize(bitmap, galleryId, null, null, null, "1", listener);
                show("Please wait..","Recognizing your face");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.ShowShortToast(getActivity(), "Failed to get the image!");
        }
    }
}
