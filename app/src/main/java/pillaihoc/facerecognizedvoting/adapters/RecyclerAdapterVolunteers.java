package pillaihoc.facerecognizedvoting.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import pillaihoc.facerecognizedvoting.pojo.Volunteers;

/**
 * Created by deepakgavkar on 15/04/16.
 */
public class RecyclerAdapterVolunteers extends RecyclerView.Adapter<RecyclerAdapterVolunteers.VolunteersHolder> {
    public static final ArrayList<String> checkedValues = new ArrayList<String>();
    private List<Volunteers> volunteers;
    View.OnClickListener ListLeaders = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VolunteersHolder holder = (VolunteersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            Volunteers volunteersModel = volunteers.get(position);
            String id = volunteersModel.getId();
            int pos = findPosition(id);
            if (pos == -1) {
                checkedValues.add(id);
            } else {
                checkedValues.remove(pos);
            }
        }
    };
    private SweetAlertDialog sweetAlertDialog;
    private RequestQueue requestQueue;
    private Context context;
    View.OnClickListener Active = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VolunteersHolder holder = (VolunteersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbBlock.setChecked(false);

            Volunteers volunteersModel = volunteers.get(position);

            String id = volunteersModel.getId();

            set("1", id);
        }
    };
    View.OnClickListener Block = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VolunteersHolder holder = (VolunteersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(false);

            Volunteers volunteersModel = volunteers.get(position);
            String id = volunteersModel.getId();

            set("0", id);
        }
    };

    public RecyclerAdapterVolunteers(Context context, List<Volunteers> volunteers) {
        this.volunteers = volunteers;
        this.context = context;
    }

    public int findPosition(String id) {
        int i = -1;
        for (int j = 0; j < checkedValues.size(); j++) {
            if (checkedValues.get(j).equals(id)) {
                i = j;
            }
        }
        return i;
    }

    public void set(String flag, String id) {
        show(context, "Requesting", "Please wait..");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", id);
        hashMap.put("flag", flag);

        if (flag.equals("0")) {
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.HANDLEACTIVATION, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
            Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.HANDLEACTIVATION + hashMap);
            requestQueue.add(jsObjRequest);
        } else if (flag.equals("1")) {
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Constants.BASE_URL + Constants.HANDLEACTIVATION, hashMap, this.createRequestSuccessListener(), this.createRequestErrorListener());
            Utils.PrintErrorLog("Parameters", "" + Constants.BASE_URL + Constants.HANDLEACTIVATION + hashMap);
            requestQueue.add(jsObjRequest);
        }
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_volunteer_card_view, null);
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
                Volunteers volunteersModel = volunteers.get(position);

                if (volunteersModel.getFlag().equals("1")) {
                    holder.rbActive.setOnClickListener(Active);
                    holder.rbActive.setTag(holder);

                    holder.rbBlock.setOnClickListener(Block);
                    holder.rbBlock.setTag(holder);

                    holder.tvVName.setText(volunteersModel.getName());
                    holder.tvPartyName.setText(volunteersModel.getPartyName());
                    holder.tvWardName.setText(volunteersModel.getWard());

                    String flag = volunteersModel.getIsActive();

                    if (flag.equals("0")) {
                        holder.rbActive.setChecked(false);
                        holder.rbBlock.setChecked(true);
                    } else if (flag.equals("1")) {
                        holder.rbActive.setChecked(true);
                        holder.rbBlock.setChecked(false);
                    }

                    Picasso.with(context)
                            .load(volunteersModel.getLogo())
                            .placeholder(R.drawable.personplaceholder)
                            .error(R.drawable.personplaceholder)
                            .into(holder.imgPartyLogo);
                } else if (volunteersModel.getFlag().equals("0")) {
                    holder.tvVName.setVisibility(View.GONE);
                    holder.tvPartyName.setVisibility(View.GONE);
                    holder.tvWardName.setVisibility(View.GONE);
                    holder.imgPartyLogo.setVisibility(View.GONE);
                    holder.rbActive.setVisibility(View.GONE);
                    holder.rbBlock.setVisibility(View.GONE);

                    holder.cbCheckUncheck.setOnClickListener(ListLeaders);
                    holder.cbCheckUncheck.setTag(holder);

                    holder.tvLeaderName.setOnClickListener(ListLeaders);
                    holder.tvLeaderName.setTag(holder);

                    holder.LinearLeaders.setVisibility(View.VISIBLE);
                    holder.tvLeaderName.setText(volunteersModel.getName() + " - " + volunteersModel.getPartyName() + " - " + volunteersModel.getWard());
                } else if (volunteersModel.getFlag().equals("2")) {
                    holder.tvVName.setVisibility(View.GONE);
                    holder.tvPartyName.setVisibility(View.GONE);
                    holder.tvWardName.setVisibility(View.GONE);
                    holder.imgPartyLogo.setVisibility(View.GONE);
                    holder.rbActive.setVisibility(View.GONE);
                    holder.rbBlock.setVisibility(View.GONE);
                    holder.CardView.setVisibility(View.GONE);

                    holder.LinearListView.setVisibility(View.VISIBLE);
                    holder.tvName.setText(volunteersModel.getName() + " - " + volunteersModel.getPartyName() + " - " + volunteersModel.getWard());
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
        return (null != volunteers ? volunteers.size() : 0);
    }

    public class VolunteersHolder extends RecyclerView.ViewHolder {

        public Context context;
        public ImageView imgPartyLogo;
        public TextView tvVName, tvPartyName, tvWardName, tvLeaderName, tvName;
        public AppCompatRadioButton rbActive, rbBlock;
        public LinearLayout LinearLeaders, LinearListView;
        public AppCompatCheckBox cbCheckUncheck;
        public CardView CardView;

        public VolunteersHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.imgPartyLogo = (ImageView) itemView.findViewById(R.id.imgPartyLogo);

            this.tvVName = (TextView) itemView.findViewById(R.id.tvVName);
            this.tvPartyName = (TextView) itemView.findViewById(R.id.tvPartyName);
            this.tvWardName = (TextView) itemView.findViewById(R.id.tvWardName);
            this.tvLeaderName = (TextView) itemView.findViewById(R.id.tvLeaderName);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);

            this.CardView = (CardView) itemView.findViewById(R.id.CardView);

            this.LinearLeaders = (LinearLayout) itemView.findViewById(R.id.LinearLeaders);
            this.LinearListView = (LinearLayout) itemView.findViewById(R.id.LinearListView);
            this.cbCheckUncheck = (AppCompatCheckBox) itemView.findViewById(R.id.cbCheckUncheck);

            this.rbActive = (AppCompatRadioButton) itemView.findViewById(R.id.rbActive);
            this.rbBlock = (AppCompatRadioButton) itemView.findViewById(R.id.rbBlock);
        }
    }
}
