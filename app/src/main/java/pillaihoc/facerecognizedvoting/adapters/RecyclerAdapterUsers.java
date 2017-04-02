package pillaihoc.facerecognizedvoting.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.helper.Constants;
import pillaihoc.facerecognizedvoting.helper.CustomRequest;
import pillaihoc.facerecognizedvoting.helper.Utils;
import pillaihoc.facerecognizedvoting.pojo.Users;

/**
 * Created by deepakgavkar on 15/04/16.
 */
public class RecyclerAdapterUsers extends RecyclerView.Adapter<RecyclerAdapterUsers.VolunteersHolder> {
    public static final ArrayList<String> checkedValues = new ArrayList<String>();
    Context context;
    RequestQueue requestQueue;
    private List<Users> users;
    private SweetAlertDialog sweetAlertDialog;

    View.OnClickListener Active = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VolunteersHolder holder = (VolunteersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbBlock.setChecked(false);

            Users user = users.get(position);

            String id = user.getId();

            set("1", id);
        }
    };
    View.OnClickListener Block = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VolunteersHolder holder = (VolunteersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(false);

            Users user = users.get(position);

            String id = user.getId();

            set("0", id);
        }
    };


    public RecyclerAdapterUsers(Context context, List<Users> users) {
        this.users = users;
        this.context = context;
    }

    public void set(String flag, String id) {
        show(context, "Requesting", "Please wait..");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("flag", flag);
        hashMap.put("id", id);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.HANDLEUSERACTIVATION, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
        Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.HANDLEUSERACTIVATION + hashMap);
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        dismissDialog();
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error);
                Utils.PrintErrorLog("Vollery Error : ", "" + error.toString());
                Utils.ShowShortToast(context, "Please try again");
            }
        };
        return err;
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog();
                Log.d("JSON Response", response.toString());
                try {
                    JSONObject object = new JSONObject(String.valueOf(response));

                    if (object.has("pass")) {
                        dismissDialog();
                        Utils.ShowShortToast(context, object.getString("pass"));
                    } else if (object.has("fail")) {
                        dismissDialog();
                        Utils.ShowShortToast(context, object.getString("fail"));
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }

    public void show(Context context, String title, String content) {
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
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
    public VolunteersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_card_view, null);
        VolunteersHolder fp = new VolunteersHolder(v);
        context = parent.getContext();
        requestQueue = Volley.newRequestQueue(parent.getContext());
        return fp;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(VolunteersHolder holder, int position) {
        try {
            try {
                Users user = users.get(position);

                holder.rbActive.setOnClickListener(Active);
                holder.rbActive.setTag(holder);

                holder.rbBlock.setOnClickListener(Block);
                holder.rbBlock.setTag(holder);


                Picasso.with(context)
                        .load(user.getProfilephoto())
                        .placeholder(R.drawable.personplaceholder)
                        .error(R.drawable.personplaceholder)
                        .into(holder.userProfile);
                holder.tvUserName.setText(user.getName());
                holder.tvWardName.setText(user.getWard());

                String flag = user.getStatus();

                if (flag.equals("0")) {
                    holder.rbActive.setChecked(false);
                    holder.rbBlock.setChecked(true);
                } else if (flag.equals("1")) {
                    holder.rbActive.setChecked(true);
                    holder.rbBlock.setChecked(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != users ? users.size() : 0);
    }

    public class VolunteersHolder extends RecyclerView.ViewHolder {

        public Context context;
        public ImageView userProfile;
        public TextView tvUserName, tvWardName;
        public AppCompatRadioButton rbActive, rbBlock;

        public VolunteersHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.userProfile = (ImageView) itemView.findViewById(R.id.userProfile);

            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvWardName = (TextView) itemView.findViewById(R.id.tvWardName);
            this.rbActive = (AppCompatRadioButton) itemView.findViewById(R.id.rbActive);
            this.rbBlock = (AppCompatRadioButton) itemView.findViewById(R.id.rbBlock);
        }
    }
}
