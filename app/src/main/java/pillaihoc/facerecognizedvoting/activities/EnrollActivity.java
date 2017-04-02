package pillaihoc.facerecognizedvoting.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.enums.SerialNumber;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;

public class EnrollActivity extends BaseActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "FaceRecognition";
    private final static int IMAGE_MAX_HEIGHT = 600, IMAGE_MAX_WIDTH = 800;
    @Bind(R.id.imgPersonImage)
    ImageView imgPersonImage;

    @Bind(R.id.btnSnapPhoto)
    Button btnSnapPhoto;

    @Bind(R.id.btnEnroll)
    Button btnEnroll;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;

    String base64PersonImage = "";
    KairosListener listener;
    Kairos myKairos;
    SweetAlertDialog sweetAlertDialog;
    ArrayList<String> data = new ArrayList<String>();
    private Uri fileUri;
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

    public static byte[] getCompressedByteArrayForImage(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] data = bos.toByteArray();
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Picasso.with(getApplicationContext())
                .load("http://www.prabhat.in/")
                .placeholder(R.drawable.personplaceholder)
                .error(R.drawable.personplaceholder)
                .into(imgPersonImage);
        try {
            data = getIntent().getStringArrayListExtra("data");
            if (data.size() < 1) {

                Utils.ShowShortToast(getApplicationContext(), "Failed to parse data, Try again!");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Utils.ShowLongToast(getApplicationContext(), "User cancelled image capture");
            } else {
                Utils.ShowLongToast(getApplicationContext(), "Sorry! Failed to capture image");
            }
        }
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

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            imgPersonImage.setImageBitmap(bitmap);
            base64PersonImage = "";
            base64PersonImage = Base64.encodeToString(getCompressedByteArrayForImage(bitmap), Base64.NO_WRAP);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnSnapPhoto)
    public void onSnapPhoto() {
        if (isDeviceSupportCamera()) {
            captureImage();
        } else {
            Utils.ShowLongToast(getApplicationContext(), "Your device doesn't support camera!");
        }
    }

    @OnClick(R.id.btnEnroll)
    public void onEnroll() {
        if (!base64PersonImage.equals("")) {
            if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                show("Registering", "Please wait..");
                registerImage(base64PersonImage);
            } else {
                Utils.ShowLongToast(getApplicationContext(), Constants.NOINTERNET);
            }
        } else {
            Utils.ShowShortToast(getApplicationContext(), "Please take your photo!");
        }
    }


    public void registerImage(final String personImage) {
        try {
            listener = new KairosListener() {

                @Override
                public void onSuccess(String response) {
                    dismissDialog();
                    Log.d("KAIROS SUCCESS", response);
                    try {
                        JSONObject root = new JSONObject(response);
                        if (root.has("images")) {
                            JSONArray images = root.getJSONArray("images");
                            JSONObject data = images.getJSONObject(0);
                            String attributes = data.getString("attributes");
                            String transaction = data.getString("transaction");

                            JSONObject jAttributes = new JSONObject(attributes);
                            JSONObject jtransaction = new JSONObject(transaction);

                            String asian = jAttributes.getString("asian");
                            String age = jAttributes.getString("age");
                            String gender = jAttributes.getString("gender");
                            String genderType = new JSONObject(gender).getString("type");

                            String status = jtransaction.getString("status");
                            String gallery_name = jtransaction.getString("gallery_name");
                            String timestamp = jtransaction.getString("timestamp");
                            String confidence = jtransaction.getString("confidence");
                            String subject_id = jtransaction.getString("subject_id");
                            String face_id = jtransaction.getString("face_id");

                            if (status.equals("success")) {
                                UpdateUserDetails(personImage, face_id, subject_id, timestamp, gallery_name, genderType, confidence);
                            }

                        } else {
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray Errors = rootObject.getJSONArray("Errors");
                            JSONObject data = Errors.getJSONObject(0);
                            String Message = data.getString("Message");
                            Utils.ShowShortToast(getApplicationContext(), Message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String response) {
                    dismissDialog();
                    Log.d("KAIROS FAIL", response);
                    Utils.ShowShortToast(getApplicationContext(), "Please try again!");
                }
            };
            myKairos = new Kairos();

            myKairos.setAuthentication(this, Constants.APP_ID, Constants.APP_KEY);
            String subjectId = (data.get(SerialNumber.LISTTOKEN) + "" + data.get(SerialNumber.LISTNAME));
            String galleryId = Constants.GALLERYID;
            myKairos.enroll(personImage, subjectId, galleryId, null, null, null, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateUserDetails(final String profilePhoto, final String faceID, final String subjectID, final String timestamp, final String galleryName, final String type, final String confidence) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", data.get(SerialNumber.LISTID));
        hashMap.put("faceID", faceID);
        hashMap.put("subjectID", subjectID);
        hashMap.put("timestamp", timestamp);
        hashMap.put("galleryName", galleryName);
        hashMap.put("type", type);
        hashMap.put("confidence", confidence);
        hashMap.put("profilePhoto", profilePhoto);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.UPDATEDETAILS, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.UPDATEDETAILS + hashMap);
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

                    if (response.has("pass")) {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
                        Intent i = new Intent(EnrollActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }
}
