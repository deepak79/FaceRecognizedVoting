package pillaihoc.facerecognizedvoting.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by deepakgavkar on 06/04/16.
 */
public class Utils {
    public static final Boolean DEBUG_STATUS = true;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static void ShowShortToast(Context view, String message) {
        Toast.makeText(view, message, Toast.LENGTH_SHORT).show();
    }

    public static void ShowShortHelpToast(Context view, String message) {
        Toast.makeText(view, "Please enter " + message, Toast.LENGTH_SHORT).show();
    }

    public static void ShowLongToast(Context view, String message) {
        Toast.makeText(view, message, Toast.LENGTH_LONG).show();
    }

    public static void PrintErrorLog(String TAG, String message) {
        if (DEBUG_STATUS) {
            Log.w(TAG, message);
        }
    }

//    public static LatLng getLocationFromAddress(Context context, String strAddress) {
//
//        Geocoder coder = new Geocoder(context);
//        List<Address> address;
//        LatLng p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress, 5);
//            if (address == null) {
//                return null;
//            }
//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//        }
//
//        return p1;
//    }
}
