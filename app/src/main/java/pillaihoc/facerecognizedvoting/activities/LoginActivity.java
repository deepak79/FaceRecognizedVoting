package pillaihoc.facerecognizedvoting.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.enums.SerialNumber;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;

/**
 * Created by deepakgavkar on 03/09/16.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.etEmail)
    MaterialEditText etEmail;

    @Bind(R.id.etPassword)
    MaterialEditText etPassword;

    @Bind(R.id.etTokenNumber)
    MaterialEditText etTokenNumber;

    @Bind(R.id.btnLogin)
    Button btnLogin;

    @Bind(R.id.rbAdmin)
    AppCompatRadioButton rbAdmin;

    @Bind(R.id.rbUser)
    AppCompatRadioButton rbUser;

    @Bind(R.id.tvNewUser)
    TextView tvNewUser;

    @Bind(R.id.btnForgot)
    Button btnForgot;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;
    ArrayList<String> dataArrayList = new ArrayList<String>();
    private SweetAlertDialog sweetAlertDialog;
    private RequestQueue requestQueue;
    private String role = "", isActive = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.rbAdmin)
    public void onAdmin() {
        rbUser.setChecked(false);
        role = "1";
    }

    @OnClick(R.id.rbUser)
    public void onUser() {
        rbAdmin.setChecked(false);
        role = "0";
    }

    @OnClick(R.id.btnLogin)
    public void onLogin() {
        if (validate() == true) {
            doLogin();
        }
    }

    @OnClick(R.id.btnForgot)
    public void onForgot() {
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @OnClick(R.id.tvNewUser)
    public void onNewUser() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public boolean validate() {
        if (etEmail.getText().toString().length() < 1) {
            etEmail.setError("Enter your email");
            return false;
        }
        if (!Utils.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Enter an valid email");
            return false;
        }
        if (etPassword.getText().toString().length() < 1) {
            etPassword.setError("Enter your password");
            return false;
        }
        if (role.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select your role");
            return false;
        }
        if (isActive.equals("0")) {
            if (etTokenNumber.getText().toString().length() < 1) {
                Utils.ShowShortToast(getApplicationContext(), "Please enter token number");
                return false;
            }
        }

        return true;
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

    public void doLogin() {
        show("Authenticating", "Please wait..");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("email", etEmail.getText().toString());
        hashMap.put("password", etPassword.getText().toString());
        hashMap.put("role", role);
        hashMap.put("tokenno", etTokenNumber.getText().toString());

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.LOGIN, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.LOGIN + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error);
                Utils.PrintErrorLog("Vollery Error : ", "" + error.toString());
                Utils.ShowShortToast(getApplicationContext(), "Please try again!");
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

                    if (role.equals("0")) {
                        if (object.has("pass")) {
                            dismissDialog();
                            Utils.ShowShortToast(getApplicationContext(), object.getString("pass"));
                            JSONObject data = new JSONObject(object.getString("data"));
                            JSONArray dataArray = data.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            isActive = "";
                            isActive = dataObject.getString("isActive");

                            if(isActive.equals("0")){
                                etTokenNumber.setVisibility(View.VISIBLE);
                            }else{
                                etTokenNumber.setVisibility(View.GONE);
                            }

                            if(isActive.equals("1"))
                            {
                                dataArrayList.add(dataObject.getString("token"));//0
                                dataArrayList.add(dataObject.getString("name"));//1
                                dataArrayList.add(dataObject.getString("profilePhoto"));//2
                                dataArrayList.add(dataObject.getString("email"));//3
                                dataArrayList.add(dataObject.getString("dob"));//4
                                dataArrayList.add(dataObject.getString("ward"));//5
                                dataArrayList.add(dataObject.getString("gender"));//6
                                dataArrayList.add(dataObject.getString("faceID"));//7
                                dataArrayList.add(dataObject.getString("subjectID"));//8
                                dataArrayList.add(dataObject.getString("id"));//9

                                storeData(getApplicationContext(), dataArrayList.get(SerialNumber.LISTEMAIL));
                                storeAllDetails(getApplicationContext(), dataArrayList);

                                if (dataObject.getString("faceID").equals("") && dataObject.getString("subjectID").equals("")) {
                                    Intent i = new Intent(LoginActivity.this, EnrollActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putStringArrayListExtra("data", dataArrayList);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putStringArrayListExtra("data", dataArrayList);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        } else if (object.has("fail")) {
                            dismissDialog();
                            Utils.ShowShortToast(getApplicationContext(), object.getString("fail"));
                        }
                    } else if (role.equals("1")) {
                        if (object.has("pass")) {
                            Utils.ShowShortToast(getApplicationContext(), object.getString("pass"));
                            Intent i = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        } else {
                            Utils.ShowShortToast(getApplicationContext(), object.getString("fail"));
                        }
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }
}
