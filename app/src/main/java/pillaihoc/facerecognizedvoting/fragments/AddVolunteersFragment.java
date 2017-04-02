package pillaihoc.facerecognizedvoting.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;

/**
 * Created by deepakgavkar on 08/09/16.
 */
public class AddVolunteersFragment extends BaseFragment {

    private static final int SELECT_PHOTO = 1;
    @Bind(R.id.imgPartyLogo)
    ImageView imgPartyLogo;

    @Bind(R.id.etVName)
    MaterialEditText etVName;

    @Bind(R.id.spWard)
    Spinner spWard;

    @Bind(R.id.spParty)
    Spinner spParty;

    @Bind(R.id.btnAddVolunteer)
    Button btnAddVolunteer;

    @Bind(R.id.LinearTouch)
    LinearLayout LinearTouch;

    String partyLogo = "";
    SweetAlertDialog sweetAlertDialog;
    ArrayList<String> listWards = new ArrayList<String>();
    ArrayList<String> listParties = new ArrayList<String>();
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_volunteers_fragment, container, false);
        ButterKnife.bind(this, view);
        setAdapter();

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Volunteers");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getActivity());

        Picasso.with(getActivity())
                .load("http://www.prabhat.in/")
                .placeholder(R.drawable.partyplaceholder)
                .error(R.drawable.partyplaceholder)
                .into(imgPartyLogo);

        return view;
    }

    @OnClick(R.id.LinearTouch)
    public void onLinearTouch() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Images"), SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                        Bitmap resized = getResizedBitmap(yourSelectedImage, 200);

                        imgPartyLogo.setImageBitmap(resized);
                        partyLogo = "";
                        partyLogo = getStringImage(resized);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @OnClick(R.id.btnAddVolunteer)
    public void onAdd() {
        if (validate() == true) {
            addVolunteer();
        }
    }

    public boolean validate() {
        if (partyLogo.equals("")) {
            Utils.ShowShortToast(getActivity(), "Please upload volunteer party logo");
            return false;
        } else if (etVName.getText().toString().length() < 1) {
            etVName.setError("Enter volunteer name");
            return false;
        } else if (spParty.getSelectedItem().toString().equals("Select")) {
            Utils.ShowShortToast(getActivity(), "Please select volunteer party name");
            return false;
        } else if (spWard.getSelectedItem().toString().equals("Select")) {
            Utils.ShowShortToast(getActivity(), "Please select volunteer ward");
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

        ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, listWards);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spWard.setAdapter(type);

        if (listParties.size() > 0) {
            listParties.clear();
        }

        listParties.add("Select");
        listParties.add("BJP");
        listParties.add("INC");
        listParties.add("CPI-M");
        listParties.add("CPI");
        listParties.add("BSP");
        listParties.add("NCP");
        listParties.add("TMC");
        listParties.add("AAP");
        listParties.add("Shiv Sena");
        listParties.add("SP");

        ArrayAdapter<String> partiesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, listParties);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spParty.setAdapter(partiesAdapter);

    }

    public void addVolunteer() {
        show("Adding", "Please wait..");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("logo", partyLogo);
        hashMap.put("name", etVName.getText().toString());
        hashMap.put("party", spParty.getSelectedItem().toString());
        hashMap.put("ward", spWard.getSelectedItem().toString());

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.ADDVOLUNTEERS, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.ADDVOLUNTEERS + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error);
                Utils.PrintErrorLog("Vollery Error : ", "" + error.toString());
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
                    JSONObject object = new JSONObject(String.valueOf(response));

                    if (object.has("pass")) {
                        Utils.ShowShortToast(getActivity(), object.getString("pass"));
                        Picasso.with(getActivity())
                                .load("http://www.prabhat.in/")
                                .placeholder(R.drawable.partyplaceholder)
                                .error(R.drawable.partyplaceholder)
                                .into(imgPartyLogo);
                        etVName.setText("");
                        spParty.setSelection(0);
                        spWard.setSelection(0);
                    } else if (object.has("fail")) {
                        Utils.ShowShortToast(getActivity(), object.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
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


}
