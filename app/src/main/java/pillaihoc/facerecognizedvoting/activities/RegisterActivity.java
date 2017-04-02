package pillaihoc.facerecognizedvoting.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * Created by deepakgavkar on 03/09/16.
 */
public class RegisterActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    @Bind(R.id.etBirthDate)
    MaterialEditText etBirthDate;

//    @Bind(R.id.etTokenNumber)
//    MaterialEditText etTokenNumber;

    @Bind(R.id.etName)
    MaterialEditText etName;

    @Bind(R.id.etEmail)
    MaterialEditText etEmail;

    @Bind(R.id.etPhone)
    MaterialEditText etPhone;

    @Bind(R.id.etPassword)
    MaterialEditText etPassword;

    @Bind(R.id.etPassword1)
    MaterialEditText etPassword1;

    @Bind(R.id.spWard)
    Spinner spWard;

    @Bind(R.id.rbMale)
    AppCompatRadioButton rbMale;

    @Bind(R.id.rbFemale)
    AppCompatRadioButton rbFemale;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Bind(R.id.tvAlreadyUser)
    TextView tvAlreadyUser;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;

    ArrayList<String> listWards = new ArrayList<String>();
    String gender = "";
    SweetAlertDialog sweetAlertDialog;
    private String tokenNumber = "";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        hideSoftKeyboard();
        setAdapter();
    }

    public void show(String title, String content) {
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
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


    public boolean validate() {
//        if (etTokenNumber.getText().toString().length() < 1) {
//            etTokenNumber.setError("Enter token number");
//            return false;
//        }
        if (etName.getText().toString().length() < 1) {
            etName.setError("Enter your name");
            return false;
        }
        if (etEmail.getText().toString().length() < 1) {
            etEmail.setError("Enter your email");
            return false;
        }
        if (!Utils.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Enter an valid email");
            return false;
        }
        if (etPhone.getText().toString().length() < 1) {
            etPhone.setError("Enter your phone number");
            return false;
        }
        if (etPhone.getText().toString().length() < 9) {
            etPhone.setError("Enter an valid phone number");
            return false;
        }
        if (etPassword.getText().toString().length() < 1) {
            etPassword.setError("Enter your password");
            return false;
        }
        if (etPassword1.getText().toString().length() < 1) {
            etPassword1.setError("retype password");
            return false;
        }
        if (!etPassword.getText().toString().equals(etPassword1.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "password not matched");
            return false;
        }
        if (etBirthDate.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please select your date of birth");
            return false;
        }

        if (spWard.getSelectedItem().toString().equals("Select")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select your ward");
            return false;
        }
        if (gender.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select your gender");
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

        ArrayAdapter<String> type = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, listWards);
        type.setDropDownViewResource(R.layout.spinner_dropdown);
        spWard.setAdapter(type);

    }

    @OnClick(R.id.btnNext)
    public void onNext() {
        if (validate() == true) {
            if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {

                registerUser();
            } else {
                Utils.ShowShortToast(getApplicationContext(), Constants.NOINTERNET);
            }
        }
    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @OnClick(R.id.rbMale)
    public void onMale() {
        gender = "";
        rbFemale.setChecked(false);
        gender = "M";
    }

    @OnClick(R.id.rbFemale)
    public void onFemale() {
        gender = "";
        rbMale.setChecked(false);
        gender = "F";
    }

    @OnClick(R.id.tvAlreadyUser)
    public void onAlready() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etBirthDate.setText("");
        String date = (++monthOfYear) + "/" + dayOfMonth + "/" + year;
        etBirthDate.setText(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @OnClick(R.id.etBirthDate)
    public void onDate() {
        showDate();
    }

    public void showDate() {
        Date date = null;
        Calendar now = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        try {
            date = formatter.parse("01/01/1990");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date != null) {
            now.setTime(date);
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                RegisterActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    //    public void validateTokenNumber() {
//        show("Registering", "Please wait..");
//        final HashMap<String, String> hashMap = new HashMap<String, String>();
//
//        //hashMap.put("token", etTokenNumber.getText().toString());
//        hashMap.put("email", etEmail.getText().toString());
//
//        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.VERIFYTOKEN, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
//        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.VERIFYTOKEN + hashMap);
//        requestQueue.add(jsObjRequest);
//    }
//
//    private Response.ErrorListener createRequestErrorListener() {
//        dismissDialog();
//        Response.ErrorListener err = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error);
//                Utils.PrintErrorLog("Vollery Error : ", "" + error.toString());
//            }
//        };
//        return err;
//    }
//
//    private Response.Listener<JSONObject> createRequestSuccessListener() {
//        dismissDialog();
//        Response.Listener listener = new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("JSON Response", response.toString());
//                try {
//                    JSONObject object = new JSONObject(String.valueOf(response));
//
//                    if (object.has("pass")) {
//                        Utils.ShowShortToast(getApplicationContext(), object.getString("pass"));
//                        data.clear();
//                        //data.add(etTokenNumber.getText().toString());
//                        data.add(etName.getText().toString());
//                        data.add(etEmail.getText().toString());
//                        data.add(etPassword.getText().toString());
//                        data.add(etBirthDate.getText().toString());
//                        data.add(spWard.getSelectedItem().toString());
//                        data.add(gender);
//                        data.add(etPhone.getText().toString());
//                        Intent i = new Intent(RegisterActivity.this, EnrollActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        i.putStringArrayListExtra("data", data);
//                        startActivity(i);
//                    } else if (object.has("fail")) {
//                        Utils.ShowShortToast(getApplicationContext(), object.getString("fail"));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        return listener;
//    }
    public void registerUser() {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("name", etName.getText().toString());
        hashMap.put("profilePhoto", "");
        hashMap.put("email", etEmail.getText().toString());
        hashMap.put("password", etPassword.getText().toString());
        hashMap.put("dob", etBirthDate.getText().toString());
        hashMap.put("ward", spWard.getSelectedItem().toString());
        hashMap.put("gender", gender);
        hashMap.put("phoneNumber", etPhone.getText().toString());
        show("Making request", "Please wait..");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.DOREGISTER, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.DOREGISTER + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error);
                dismissDialog();
                Utils.ShowShortToast(getApplicationContext(), "Please try again!");
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
                dismissDialog();
                Log.d("JSON Response", response.toString());
                try {
                    JSONObject object = new JSONObject(String.valueOf(response));

                    if (object.has("pass")) {
                        Utils.ShowShortToast(getApplicationContext(), object.getString("pass"));
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Utils.ShowShortToast(getApplicationContext(), object.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }

}
