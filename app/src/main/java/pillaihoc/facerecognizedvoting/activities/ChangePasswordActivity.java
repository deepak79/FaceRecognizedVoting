package pillaihoc.facerecognizedvoting.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

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
 * Created by deepakgavkar on 08/09/16.
 */
public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.etPassword)
    MaterialEditText etPassword;

    @Bind(R.id.etNewPassword)
    MaterialEditText etNewPassword;

    @Bind(R.id.etNewPassword1)
    MaterialEditText etNewPassword1;

    @Bind(R.id.btnChangePassword)
    Button btnChangePassword;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;

    private SweetAlertDialog sweetAlertDialog;
    private RequestQueue requestQueue;
    private ArrayList<String> userData = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userData = getAllDetails(getApplicationContext());
    }

    @OnClick(R.id.btnChangePassword)
    public void onChangePass() {
        if (validate() == true && userData != null && userData.size() > 0) {
            changePassword();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userData = getAllDetails(getApplicationContext());
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
        if (etPassword.getText().toString().length() < 1) {
            etPassword.setError("Enter your current password");
            return false;
        }
        if (etNewPassword.getText().toString().length() < 1) {
            etPassword.setError("Enter your new password");
            return false;
        }
        if (etNewPassword1.getText().toString().length() < 1) {
            etPassword.setError("Retype new password");
            return false;
        }
        if (!etNewPassword.getText().toString().equals(etNewPassword1.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "password not matched");
            return false;
        }
        return true;
    }

    public void changePassword() {
        show("Verifying", "Please wait..");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", userData.get(SerialNumber.LISTID));
        hashMap.put("password", etPassword.getText().toString());
        hashMap.put("newpassword", etNewPassword.getText().toString());

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.CHANGEPASSWORD, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.CHANGEPASSWORD + hashMap);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ChangePasswordActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("profile","1");
        startActivity(i);
        this.overridePendingTransition(0,0);
        finish();
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
                        Utils.ShowShortToast(getApplicationContext(), object.getString("pass"));
                        logout(getApplicationContext());
                        Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else if (object.has("fail")) {
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
