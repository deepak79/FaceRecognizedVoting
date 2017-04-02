package pillaihoc.facerecognizedvoting.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.Utils;


/**
 * Created by deepakgavkar on 20/04/16.
 */
public class ForgotPasswordActivity extends BaseActivity {

    @Bind(R.id.etForgotEmail)
    EditText etForgotEmail;

    @Bind(R.id.btnResetPassword)
    Button btnResetPassword;

    @Bind(R.id.tvResetSuccess)
    TextView tvResetSuccess;

    @Bind(R.id.tvEmail)
    TextView tvEmail;

    @Bind(R.id.tvEnterEmail)
    TextView tvEnterEmail;

    @Bind(R.id.tvPlease)
    TextView tvPlease;

    @Bind(R.id.btnLoginAgain)
    Button btnLoginAgain;
    String txtEmail;
    private RequestQueue requestQueue;
    private StringRequest request;
    private SweetAlertDialog sweetAlertDialog;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    @OnClick(R.id.btnResetPassword)
    public void reset() {
        if (etForgotEmail.getText().toString().length() == 0) {
            Utils.ShowShortHelpToast(getApplicationContext(), "email");
        } else if (!Utils.isValidEmail(etForgotEmail.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Please provide valid email");
        } else if (Utils.getConnectivityStatus(getApplicationContext()) == 0) {
            Utils.ShowShortToast(getApplicationContext(), Constants.NOINTERNET);
        } else if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
            txtEmail = etForgotEmail.getText().toString();
            ResetPassword();
        }
    }

    @OnClick(R.id.btnLoginAgain)
    public void Login() {
        Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
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

    public void ResetPassword() {
        show("Requesting", "Please wait..");
        request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.FORGOTPASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.PrintErrorLog("FORGOT PASSWORD", "" + response);
                dismissDialog();
                try {
                    dismissDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("pass")) {

                        tvEnterEmail.setVisibility(View.GONE);
                        etForgotEmail.setVisibility(View.GONE);
                        btnResetPassword.setVisibility(View.GONE);

                        tvResetSuccess.setVisibility(View.VISIBLE);
                        tvEmail.setVisibility(View.VISIBLE);

                        tvEmail.setText(txtEmail);
                        tvPlease.setVisibility(View.VISIBLE);

                        btnLoginAgain.setVisibility(View.VISIBLE);

                    } else if (jsonObject.has("fail")) {
                        Utils.ShowShortToast(getApplicationContext(), jsonObject.getString("fail"));
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
                Utils.ShowShortToast(getApplicationContext(), Constants.ISSUE);
                Utils.PrintErrorLog("Volley Error : ", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("email", etForgotEmail.getText().toString());

                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }
}
